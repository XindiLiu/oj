package com.example.oj.exception;

import com.example.oj.entity.Submission;

public class CodeTestingException extends RuntimeException {
	protected Submission submission;

	public Submission getSubmission() {
		return submission;
	}

	public CodeTestingException() {
	}

	public CodeTestingException(String message) {
		super(message);
	}

	public CodeTestingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodeTestingException(Throwable cause) {
		super(cause);
	}

	public CodeTestingException(Submission submission) {
		this.submission = submission;
	}

	public CodeTestingException(String message, Submission submission) {
		super(message);
		this.submission = submission;
	}

	public CodeTestingException(String message, Throwable cause, Submission submission) {
		super(message, cause);
		this.submission = submission;
	}

	public CodeTestingException(Throwable cause, Submission submission) {
		super(cause);
		this.submission = submission;
	}

}
