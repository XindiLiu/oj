package com.example.oj.controller;

import com.example.oj.DTO.TestCaseResponseDTO;
import com.example.oj.common.Result;
import com.example.oj.entity.TestCase;
import com.example.oj.exception.FileTypeException;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.filesystem.FileService;
import com.example.oj.entity.Problem;
import com.example.oj.service.ProblemService;
import com.example.oj.service.TestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
public class TestCaseController {

	private final TestCaseService testCaseService;
	private final ProblemService problemService;
	private final FileService fileService;

	public TestCaseController(TestCaseService testCaseService,
							  ProblemService problemService,
							  FileService fileService) {
		this.testCaseService = testCaseService;
		this.problemService = problemService;
		this.fileService = fileService;
	}

	/*
	 * Upload test cases for a problem. This will replace all existing test cases for the problem.
	 * Requirements:
	 * 1. The test data must be compressed in a zip file.
	 * 2. All test cases must be in the first level of the zip file.
	 * 3. Input and output files must appear in pairs, with extensions .in and .out respectively.
	 * 4. No subdirectories or irrelevant files should be in the zip file.
	 */
	@PostMapping("/problem/{problemId}/testCases")
	@ResponseBody
	public Result<List<TestCaseResponseDTO>> save(
			@PathVariable Long problemId,
			@RequestParam("testCaseZipFile") MultipartFile testCaseZipFile) throws IdNotFoundException {

		List<TestCase> testCases;
		try {
			testCases = testCaseService.saveTestCases(testCaseZipFile, problemId);
		} catch (IOException e) {
			log.error("Failed to save test cases:{}", e.getMessage());
			return Result.fail();
		} catch (FileTypeException e) {
			log.error(e.getMessage());
			return Result.fail();
		}

		List<TestCaseResponseDTO> responseDTOs = testCases.stream()
				.map(TestCaseResponseDTO::new)
				.toList();
		return Result.success(responseDTOs);
	}

	/*
	 * Get all test cases for a problem, excluding the input and output.
	 */
	@GetMapping("/problem/{problemId}/testCases")
	@ResponseBody
	public Result<List<TestCase>> getByProblemId(@PathVariable Long problemId) {
		//    public Result save(@RequestBody TestCase testCase){
		List<TestCase> testCases = testCaseService.getByProblemId(problemId);
		return Result.success(testCases);
	}

	// @DeleteMapping("/testcase/{id}")
	// @ResponseBody
	// public Result delete(@PathVariable Long testCaseId) {
	// 	testCaseService.delete(testCaseId);
	// 	return Result.success();
	// }

}
