package com.example.oj.exception;

public class FileTypeException extends RuntimeException {
	public FileTypeException() {
	}

	public FileTypeException(String message) {
		super(message);
	}

	public FileTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileTypeException(Throwable cause) {
		super(cause);
	}

	public FileTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
