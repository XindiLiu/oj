package com.example.oj.exception;

public class AlreadyLoggedInException extends Exception {
	public AlreadyLoggedInException(Long userId, String username) {
		super(String.format("Already logged in as id:{}, username:{}", userId, username));
	}

	public AlreadyLoggedInException() {
		super(String.format("Already logged in"));
	}
}
