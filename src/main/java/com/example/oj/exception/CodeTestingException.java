package com.example.oj.exception;

public class CodeTestingException extends Exception {
	public CodeTestingException(Throwable cause) {
		super(cause);
	}

	public CodeTestingException(String message) {
		super(message);
	}

	public CodeTestingException() {
		super();
	}

	public CodeTestingException(String message, Throwable cause) {
		super(message, cause);
	}
}
