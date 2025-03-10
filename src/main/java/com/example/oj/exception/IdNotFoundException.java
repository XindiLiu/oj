package com.example.oj.exception;

// When updating an entity, and it is not found in the database.
public class IdNotFoundException extends RuntimeException {
	public IdNotFoundException(Class<?> clazz, Object id) {
		super(String.format("%s with id %s not found", clazz.getSimpleName(), id));
	}

	public IdNotFoundException() {
	}

	public IdNotFoundException(String message) {
		super(message);
	}
}
