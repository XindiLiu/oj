package com.example.oj.exception;

public class IdNotFoundException extends Exception {
	public IdNotFoundException(Class<?> clazz, Object id) {
		super(String.format("%s with id %s not found", clazz.getSimpleName(), id));
	}
}
