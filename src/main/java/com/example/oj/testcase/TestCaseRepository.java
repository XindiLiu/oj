package com.example.oj.testcase;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface TestCaseRepository extends CrudRepository<TestCase, Long> {
	TestCase save(TestCase testCase);

	void deleteByTestCaseId(Long testCase);

	List<TestCase> getByProblemId(Long problemId);

	ArrayList<TestCase> getByProblemIdOrderByName(Long problemId);

	void deleteByProblemId(Long problemId);

}
