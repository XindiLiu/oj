package com.example.oj.codeTester;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.filesystem.FileService;
import com.example.oj.problem.Problem;
import com.example.oj.submission.Submission;
import com.example.oj.submission.SubmissionStatus;
import com.example.oj.testcase.TestCase;
import com.example.oj.testcase.TestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class LocalDockeStdioCodeTester implements CodeTester {
	@Autowired
	TestCaseService testCaseService;
	@Value("${docker.image}")
	String dockerImage;

	@Value("${docker.mountFolder}")
	String dockerMountWorkspace;

	@Value("${docker.workspace}")
	String dockerWorkspace;
	@Autowired
	FileService fileService;

	// TODO: Handle exceptions.
	@Override
	public Submission test(Problem problem, Submission submission) throws IOException, InterruptedException {
		Path dockerMountFolder = null;
		try {
			//		Path dockerMountFolder = fileService.mkTempDir(dockerMountWorkspace, submission.getFileName()).toAbsolutePath();
			dockerMountFolder = fileService.mkTempDir(dockerMountWorkspace).toAbsolutePath();
			int timeLimit = problem.getProblemDetail().getTimeLimitSeconds();
			int memoryLimitMB = problem.getProblemDetail().getMemoryLimitMB();
			log.info("Created temp directory: {}", dockerMountFolder);
			fileService.writeFile(dockerMountFolder.resolve(submission.getFileName()), submission.getCode());

			// Compilation
			List<String> compileCmd = List.of("docker", "run", "--rm", "-v",
					dockerMountFolder.toString() + ":/" + dockerWorkspace,
					dockerImage, "\"compiler " + submission.getFileName() + " a.exe\"");
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
					log.error("Docker error: {}", ceMessage);
					submission.setJudgement(SubmissionResultType.JE);
					submission.setStatus(SubmissionStatus.FAILED);
				} else {
					submission.setMessage(ceMessage);
					submission.setJudgement(SubmissionResultType.CE);
					submission.setStatus(SubmissionStatus.FINISHED);
				}
				fileService.rmDir(dockerMountFolder);
				return submission;
			}
			// Run testcases
			List<Long> runTimes = new ArrayList<>(); // ms
			List<Long> memory = new ArrayList<>(); // byte
			List<String> exeCmd = List.of("docker", "run", "-i", "--rm", "-v", // Add -i to make the process wait for input
					dockerMountFolder.toString() + ":/" + dockerWorkspace,
					dockerImage, String.format("runner_stdio a.exe %d %d", timeLimit, memoryLimitMB));

			log.info("Running test cases, execution command: {}", String.join(" ", exeCmd));
			List<TestCase> testCases = testCaseService.getByProblemId(problem.getId());
			submission.setTotalCases(testCases.size());
			int nPassedCases = 0; // Loop counter
			for (TestCase testCase : testCases) {
				// Start process
				//			Process exeProcess = new ProcessBuilder(exeCmd).redirectInput(new File(testCase.getInputPath())).start();
				Process exeProcess = new ProcessBuilder(exeCmd).redirectInput(new File(testCase.getInputPath()))
						.redirectError(ProcessBuilder.Redirect.INHERIT).start();

				// Read output from the process
				BufferedReader outputReader = new BufferedReader(new InputStreamReader(exeProcess.getInputStream()));
				List<String> outputs = new ArrayList<>();
				while ((line = outputReader.readLine()) != null) {
					outputs.add(line);
				}
				outputReader.close();
				int exeStatus = exeProcess.waitFor();

				if (outputs.isEmpty()) {
					// TODO: exception
				}
				String judgemnt = outputs.get(0);
				if (judgemnt != null && !judgemnt.equals("AC")) { // RE, TLE, MLE
					try {
						submission.setJudgement(SubmissionResultType.valueOf(judgemnt));
					} catch (IllegalArgumentException e) {
						// TODO: handle exception
					}
					break;
				} else { // Program terminated correctly (AC or WA)
					if (outputs.size() < 4) {
						// TODO: exception
					}
					try {
						runTimes.add(Long.parseLong(outputs.get(1)));
						memory.add(Long.parseLong(outputs.get(2)));
					} catch (NumberFormatException e) {
						// TODO: handle exception
					}

					String output = String.join("\n", outputs.subList(3, outputs.size()));
					// Compare with correct answer
					String correctOutput = Files.readString(Path.of(testCase.getOutputPath()));
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
			return submission;
		} catch (IOException e) {
			submission.setJudgement(SubmissionResultType.JE);
			submission.setStatus(SubmissionStatus.FAILED);
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			if (dockerMountFolder != null) {
				fileService.rmDir(dockerMountFolder);
			}
		}

	}

}
