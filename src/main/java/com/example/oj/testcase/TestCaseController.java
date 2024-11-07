package com.example.oj.testcase;

import com.example.oj.common.Result;
import com.example.oj.problem.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class TestCaseController {
	@Autowired
	TestCaseService testCaseService;
	@Autowired
	ProblemService problemRService;

	@PostMapping("/problem/{problemId}/testCases")
	@ResponseBody
	public Result<List<TestCaseResponseDTO>> save(
			@PathVariable Long problemId,
			@RequestParam("testCaseZipFile") MultipartFile testCaseZipFile) {
		try {
			List<TestCase> testCases = testCaseService.saveTestCases(testCaseZipFile, problemId);
			List<TestCaseResponseDTO> responseDTOs = testCases.stream()
					.map(TestCaseResponseDTO::new)
					.toList();
			return Result.success(responseDTOs);
		} catch (IOException e) {
			log.error("Failed to save test case files: {}", e.getMessage());
			return Result.fail("Failed to save test case files");
		}
	}

	@GetMapping("/problem/{problemId}/testcases")
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
