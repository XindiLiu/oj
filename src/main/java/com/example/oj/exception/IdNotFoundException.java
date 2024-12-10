package com.example.oj.exception;

public class IdNotFoundException extends Exception {
	// When updating an entity, and it is not found in the database.
	public IdNotFoundException(Class<?> clazz, Object id) {
		super(String.format("%s with id %s not found", clazz.getSimpleName(), id));
	}
}
