package com.example.oj.exception;

import com.example.oj.entity.Submission;

public class CodeTesterUnavailableException extends RuntimeException {
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
