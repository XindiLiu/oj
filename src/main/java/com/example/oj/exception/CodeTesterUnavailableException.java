package com.example.oj.exception;

import com.example.oj.submission.Submission;

public class CodeTesterUnavailableException extends Exception {
	protected Submission submission;

	public Submission getSubmission() {
		return submission;
	}
	
	public CodeTesterUnavailableException(Submission submission) {
		this.submission = submission;
	}

	public CodeTesterUnavailableException(String message, Submission submission) {
		super(message);
		this.submission = submission;
	}

	public CodeTesterUnavailableException(Throwable cause, Submission submission) {
		super(cause);
		this.submission = submission;
	}

	public CodeTesterUnavailableException() {
	}

	public CodeTesterUnavailableException(String message) {
		super(message);
	}

	public CodeTesterUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodeTesterUnavailableException(Throwable cause) {
		super(cause);
	}
}
