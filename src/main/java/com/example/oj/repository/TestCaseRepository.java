package com.example.oj.repository;

import com.example.oj.entity.TestCase;
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
