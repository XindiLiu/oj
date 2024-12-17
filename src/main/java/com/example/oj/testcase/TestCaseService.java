package com.example.oj.testcase;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TestCaseService {
	public void save(@RequestBody TestCase testCase);

	public List<TestCase> getByProblemId(Long problemId);

	public List<TestCase> saveTestCases(MultipartFile zipFile, Long problemId);
}
