package com.example.oj.exception;

public class CodeTesterUnavailableException extends Exception {
	public CodeTesterUnavailableException(Exception e) {
		super(e);
	}

	public CodeTesterUnavailableException(String msg) {
		super(msg);
	}

	public CodeTesterUnavailableException() {
		super();
	}

	public CodeTesterUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
