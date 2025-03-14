package com.example.oj.codetester;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.exception.CodeTesterUnavailableException;
import com.example.oj.exception.CodeTestingException;
import com.example.oj.filesystem.FileService;
import com.example.oj.entity.Problem;
import com.example.oj.entity.Submission;
import com.example.oj.constant.SubmissionStatus;
import com.example.oj.entity.TestCase;
import com.example.oj.service.TestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Component
@Slf4j
public class DockerCodeTester implements CodeTester {

	private final TestCaseService testCaseService;
	private final FileService fileService;

	@Value("${DockerCodeTester.docker.image}")
	private String dockerImage;

	@Value("${DockerCodeTester.docker.mountFolder}")
	private String dockerMountWorkspace;

	@Value("${DockerCodeTester.docker.workspace}")
	private String dockerWorkspace;

	@Value("${DockerCodeTester.compiledFileName}")
	private String compiledFileName;

	public DockerCodeTester(TestCaseService testCaseService, FileService fileService) {
		this.testCaseService = testCaseService;
		this.fileService = fileService;
	}

	/*
	 * 1. Create a temp folder as the workspace for the submission.
	 * 2. Compile the submission.
	 * 3. Test the submission.
	 * 4. Delete the temp folder after testing.
	 * @param problem
	 * @param submission
	 * @param afterCodeTesting Actions after code testing, such as updating the submission.
	 * @throws CodeTesterUnavailableException If docker daemon is not started.
	 * @throws CodeTestingException If there is an error on file IO or the code testing program.
	 */
	@Override
	@Async
	public void test(Problem problem, Submission submission, Consumer<Submission> afterCodeTesting) {
		Path tempFolder = null;
		try {
			tempFolder = fileService.mkTempDir(dockerMountWorkspace).toAbsolutePath();
			log.info("Created temp directory: {}", tempFolder);
			Path tempCodeFile = tempFolder.resolve(submission.getFileName());
			fileService.writeFile(tempCodeFile, submission.getCode());
			compile(tempFolder, tempCodeFile, submission);
			submission = testSubmission(problem, submission, tempFolder, tempFolder.resolve(compiledFileName));
			log.info("test finished: {}", submission.getId());
		} catch (IOException e) {
			log.error("Error on file IO: {}", e.getMessage());
			submission.setStatus(SubmissionStatus.FAILED);
			submission.setJudgement(SubmissionResultType.JE);
		} catch (InterruptedException e) {
			log.error("Error on the code testing process: {}", e.getMessage());
			submission.setStatus(SubmissionStatus.FAILED);
			submission.setJudgement(SubmissionResultType.JE);
		} catch (CodeTestingException | CodeTesterUnavailableException e) {
			log.error(e.getMessage());
			submission.setStatus(SubmissionStatus.FAILED);
			submission.setJudgement(SubmissionResultType.JE);
		} finally {
			if (tempFolder != null) {
				try {
					fileService.rmDir(tempFolder);
				} catch (IOException e) {
					// Use unchecked exception to avoid rollback, handled in global exception handler.
					log.error("Failed to delete temp folder {} after code testing: {}", tempFolder, e.getMessage());
					//					throw new RuntimeException("Failed to delete temp folder after code testing.", e);
				}
			}
		}
		afterCodeTesting.accept(submission);
	}

	private Submission testSubmission(Problem problem, Submission submission, Path tempFolder, Path compiledFile)
			throws CodeTestingException, CodeTesterUnavailableException, IOException, InterruptedException {
		// Run testcases
		int timeLimit = problem.getProblemDetail().getTimeLimitSeconds();
		int memoryLimitMB = problem.getProblemDetail().getMemoryLimitMB();
		List<Long> runTimes = new ArrayList<>(); // ms
		List<Long> memory = new ArrayList<>(); // byte
		List<String> exeCmd = List.of("docker", "run", "-i", "--rm", "-v", // Add -i to make the process wait for input
				tempFolder.toString() + ":/" + dockerWorkspace,
				dockerImage, String.format("runner_stdio a.exe %d %d", timeLimit * 1000, memoryLimitMB));

		log.info("Running test cases, execution command: {}", String.join(" ", exeCmd));

		// Read from database, otherwise cannot get input and output since the input and output are lazy loaded.
		List<TestCase> testCases = testCaseService.getByProblemIdWithInOut(problem.getId());
		//		List<TestCase> testCases = problem.getTestCases();

		submission.setTotalCases(testCases.size());
		int nPassedCases = 0; // Loop counter
		for (TestCase testCase : testCases) {
			// Start process
			//			Process exeProcess = new ProcessBuilder(exeCmd).redirectInput(new File(testCase.getInputPath())).start();
			byte[] input = testCase.getInput().getBytes();
			Process exeProcess = new ProcessBuilder(exeCmd)
					.redirectError(ProcessBuilder.Redirect.INHERIT)
					//					.redirectInput(new File(testCase.getInputPath()))
					.start();
			exeProcess.getOutputStream().write(input);
			exeProcess.getOutputStream().close(); // Must close the output stream.

			// Read output from the process
			// The output should consist of at least four lines: judgement, time, memory, output from the submitted code.
			BufferedReader outputReader = new BufferedReader(new InputStreamReader(exeProcess.getInputStream()));
			List<String> outputs = new ArrayList<>();
			String line;
			while ((line = outputReader.readLine()) != null) {
				outputs.add(line);
			}
			outputReader.close();
			int exeStatus = exeProcess.waitFor();
			if (outputs.isEmpty()) {
				throw new CodeTestingException("No output from the code tester");
			}
			String judgement = outputs.get(0);
			if (judgement != null && !judgement.equals("AC")) { // RE, TLE, MLE
				try {
					submission.setJudgement(SubmissionResultType.valueOf(judgement));
				} catch (IllegalArgumentException e) {
					throw new CodeTestingException(
							String.format("Unknown judgement from the code tester: %s", judgement), e);
				}
				break;
			} else { // Program terminated correctly (AC or WA)
				if (outputs.size() < 4) {
					throw new CodeTestingException(
							"Wrong output format from the code tester, should be at least four lines (judgement, time, memory, answer), got:"
									+ String.join("\n", outputs));
				}
				try {
					runTimes.add(Long.parseLong(outputs.get(1)));
				} catch (NumberFormatException e) {
					throw new CodeTestingException(
							"Wrong format of run time, expect an integer but got" + outputs.get(1), e);
				}
				try {
					memory.add(Long.parseLong(outputs.get(2)));
				} catch (NumberFormatException e) {
					throw new CodeTestingException("Wrong format of memory, expect an integer but got" + outputs.get(2),
							e);
				}
				String output = String.join("\n", outputs.subList(3, outputs.size()));

				// Compare with correct answer
				//				String correctOutput = Files.readString(Path.of(testCase.getOutputPath()));\
				String correctOutput = testCase.getOutput();

				if (!output.stripTrailing().equals(correctOutput.stripTrailing())) { // Wrong answer
					submission.setJudgement(SubmissionResultType.WA);
					outputReader.close();
					exeProcess.destroy();
					break;
				}
				nPassedCases++;
			}
		}
		submission.setNumPassedCases(nPassedCases);
		submission.setStatus(SubmissionStatus.FINISHED);
		if (nPassedCases == submission.getTotalCases()) {
			submission.setRunTimeMs(Collections.max(runTimes));
			submission.setMemoryByte(Collections.max(memory));
			submission.setJudgement(SubmissionResultType.AC);
		}
		log.info("passed {} of {} cases: {}", nPassedCases, submission.getTotalCases(),
				submission.getJudgement());
		//			fileService.rmDir(dockerMountFolder);
		//			return submission;

		return submission;
	}

	private void compile(Path tempFolder, Path tempCodeFile, Submission submission)
			throws IOException, InterruptedException, CodeTesterUnavailableException {
		List<String> compileCmd = List.of("docker", "run", "--rm", "-v",
				tempFolder.toString() + ":/" + dockerWorkspace,
				dockerImage, "\"compiler " + submission.getFileName() + " " + compiledFileName + "\"");

		log.info("Start compilation: {}", String.join(" ", compileCmd));

		Process compileProcess = new ProcessBuilder(compileCmd).start();
		BufferedReader ceReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
		StringBuilder stderr = new StringBuilder();
		String line;
		while ((line = ceReader.readLine()) != null) {
			stderr.append(line).append("\n");
		}
		String ceMessage = stderr.toString();
		int compileStatus = compileProcess.waitFor();
		if (compileStatus != 0) {
			if (ceMessage.startsWith("docker")) {
				// Docker daemon not started
				log.error("Docker error: {}", ceMessage);
				throw new CodeTesterUnavailableException("Docker error: {}");
			} else {
				submission.setMessage(ceMessage);
				submission.setJudgement(SubmissionResultType.CE);
				submission.setStatus(SubmissionStatus.FINISHED);
			}
		}
	}

}
