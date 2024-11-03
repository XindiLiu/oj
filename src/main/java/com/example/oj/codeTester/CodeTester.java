package com.example.oj.codeTester;

import com.example.oj.problem.Problem;
import com.example.oj.submission.Submission;

import java.io.IOException;

public interface CodeTester {
	public Submission test(Problem problem, Submission submission) throws IOException, InterruptedException;


}
