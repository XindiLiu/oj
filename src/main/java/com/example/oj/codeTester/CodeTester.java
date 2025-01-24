package com.example.oj.codeTester;

import com.example.oj.entity.Problem;
import com.example.oj.entity.Submission;
import org.springframework.scheduling.annotation.Async;

import java.util.function.Consumer;

public interface CodeTester {


	// TODO: Handle exceptions.
	@Async
	abstract void test(Problem problem, Submission submission, Consumer<Submission> afterCodeTesting);
}
