package com.example.oj.service;

import com.example.oj.entity.Problem;
import com.example.oj.entity.TestCase;
import com.example.oj.exception.FileTypeException;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.filesystem.FileService;
import com.example.oj.repository.ProblemRepository;
import com.example.oj.repository.TestCaseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Transactional 存储多个表
@Service
@Slf4j
@RequiredArgsConstructor
public class TestCaseService {
	private final TestCaseRepository testCaseRepository;
	private final ProblemRepository problemRepository;
	private final FileService fileService;
	@Value("${filesys.testCaseDir}")
	private Path testCasesPath;

	public ArrayList<TestCase> getByProblemId(Long problemId) {
		return testCaseRepository.getByProblemIdOrderByName(problemId);
	}

	/*
	 * Input and output are lazy loaded, explicitly set them.
	 * Need to ensure that test cases are executed in correct order.
	 */
	public ArrayList<TestCase> getByProblemIdWithInOut(Long problemId) {
		ArrayList<TestCase> testCases = testCaseRepository.getByProblemIdOrderByName(problemId);
		for (TestCase testCase : testCases) {
			testCase.setInput(testCase.getInput());
			testCase.setOutput(testCase.getOutput());
		}
		return testCases;
	}

	public void delete(Long testCaseId) {
		testCaseRepository.deleteByTestCaseId(testCaseId);
	}

	/*
	 * Unzip the file in a temporary directory, and save the test cases to the database.
	 */
	@Transactional
	public List<TestCase> saveTestCases(MultipartFile zipFile, Long problemId) throws IOException, IdNotFoundException, FileTypeException {
		if (problemRepository.getById(problemId) == null) {
			throw new IdNotFoundException(Problem.class, problemId);
		}
		// Validate that the uploaded file is a ZIP
		if (zipFile.isEmpty() || !fileService.isZip(zipFile)) {
			throw new FileTypeException("Empty file or the file is not a zip file");
		}
		Path zipPath = fileService.extractZip(zipFile);

		// Match in and out files
		File[] inputs = zipPath.toFile().listFiles((f) -> {
			return f.toString().endsWith(".in") && !f.isDirectory();
		});
		File[] outputs = zipPath.toFile().listFiles((f) -> {
			return f.toString().endsWith(".out") && !f.isDirectory();
		});

		// If no valid input or output file in the zipfile
		if (inputs.length == 0 || outputs.length == 0) {
			return null;
		}
		Set<String> inputNames = Arrays.stream(inputs).map((f) -> FilenameUtils.getBaseName(f.getName()))
				.collect(Collectors.toSet());
		Arrays.sort(outputs);
		List<TestCase> testCaseList = new ArrayList<>();

		// Create TestCase objects if there is a match between in and out, add to list.
		for (File output : outputs) {
			String testCaseName = FilenameUtils.getBaseName(output.getName());
			if (inputNames.contains(testCaseName)) {
				TestCase testCase = new TestCase();
				testCase.setProblem(problemRepository.getById(problemId));
				testCase.setName(testCaseName);
				testCase.setInput(Files.readString(zipPath.resolve(testCaseName + ".in")));
				testCase.setOutput(Files.readString(zipPath.resolve(testCaseName + ".out")));
				testCaseList.add(testCase);
			}
		}
		if (testCaseList.isEmpty()) {
			return null;
		}
		// Update database
		testCaseRepository.deleteByProblemId(problemId);
		testCaseRepository.saveAll(testCaseList);
		//		fileService.rmDir(zipPath);

		return testCaseList;


	}
}
