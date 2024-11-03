package com.example.oj.testcase;

import com.example.oj.problem.Problem;
import com.example.oj.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TestCaseRepository extends CrudRepository<TestCase, Long> {
	TestCase save(TestCase testCase);

	void deleteByTestCaseId(Long testCase);

	List<TestCase> getByProblemId(Long problemId);

	void deleteByProblemId(Long problemId);

}
