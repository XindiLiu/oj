/*
package com.example.oj.codeTester;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.filesystem.FileService;
import com.example.oj.problem.Problem;
import com.example.oj.submission.Submission;
import com.example.oj.submission.SubmissionResult;
import com.example.oj.testcase.TestCase;
import com.example.oj.testcase.TestCaseRepository;
import com.example.oj.testcase.TestCaseService;
import org.hibernate.Remove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Component
public class LocalDockerCodeTester implements CodeTester {
	@Autowired
	TestCaseService testCaseService;
	@Value("${docker.image}")
	private String dockerImage;

	@Value("${docker.mountFolder}")
	private String dockerMountWorkspace;

	@Value("${docker.workspace}")
	private String dockerWorkspace;
	@Autowired
	FileService fileService;

	// private final Submission submission;
	// int timeLimit;
	// int memoryLimit;

	private String dockerComplieCmd(Submission submission) {
		return String.format("docker run --rm -v %s:/%s %s \"compiler %d a 2> CE\"", dockerMountFolder, dockerWorkspace,
				dockerImage, this.submission.getId());
	}

	private String exeCmd(String testCase, Long timeLimit, Long memoryLimit) {
		return String.format("docker run --rm -v %s:/%s %s \"runner a %s.in %s.out %s.result %d %d\"",
				dockerMountFolder, dockerWorkspace, dockerImage, testCase, testCase, testCase, timeLimit,
				memoryLimit);
	}

	// TODO: Handle exceptions.
	@Override
	public SubmissionResult test(Problem problem, Submission submission) throws IOException, InterruptedException {
		SubmissionResult submissionResult = new SubmissionResult();
		String dockerMountFolder = fileService.mkTempDir(dockerMountWorkspace, submission.getFileName()).toString();
		int timeLimit = problem.getProblemDetail().getTimeLimitSeconds();
		int memoryLimit = problem.getProblemDetail().getMemoryLimitMB();
		String compileCommand = dockerComplieCmd();
		Process compileProcess = new ProcessBuilder(compileCommand).start();
		int status = compileProcess.waitFor();
		if (status != 0) {
			submissionResult.setSubmissionResultType(SubmissionResultType.CE);
			String ceMessage = java.nio.file.Files.readString(java.nio.file.Paths.get("CE.txt"));
			submissionResult.setMessage(ceMessage);
			return submissionResult;
		}

		List<Long> runTimes = new ArrayList<>();
		List<Long> memorys = new ArrayList<>();
		for (TestCase testCase : testCaseService.getByProblemId(problem.getId())) {
			String exeCommand = exeCmd(testCase);
			Process exeProcess = new ProcessBuilder(exeCommand).start();
			status = exeProcess.waitFor();
			List<String> resultString = java.nio.file.Files
					.readAllLines(java.nio.file.Paths.get(String.format("%s.result", testCase)));
			SubmissionResultType resultType = SubmissionResultType.valueOf(resultString.get(2));
			if (status == 0) {
				runTimes.add(Long.parseLong(resultString.get(0)));
				memorys.add(Long.parseLong(resultString.get(1)));

			} else {
				submissionResult.setSubmissionResultType(resultType);
				return submissionResult;
			}
		}
		submissionResult.setSubmissionResultType(SubmissionResultType.AC);
		submissionResult.setRunTime(Collections.max(runTimes));
		submissionResult.setMemory(Collections.max(memorys));
		return submissionResult;

	}

}
*/
