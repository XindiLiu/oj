package com.example.oj.submission;

public enum SubmissionStatus {
	// The server has received the submission
	SUBMITTED,
	// The server is testing the solution with test cases
	RUNNING,
	FAILED,
	// The solution is tested. A SubmissionResult is given to the user.
	FINISHED;
}
