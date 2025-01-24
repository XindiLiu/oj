package com.example.oj.constant;

public enum SubmissionStatus {
	// The server has received the submission
	SUBMITTED,
	// The server is testing the solution with test cases
	RUNNING,
	// Error on the code tester.
	FAILED,
	// The solution is tested. A SubmissionResult is given to the user.
	FINISHED;
}
