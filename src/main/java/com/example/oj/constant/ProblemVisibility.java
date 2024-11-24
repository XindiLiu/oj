package com.example.oj.constant;

public enum ProblemVisibility {
	PUBLIC("public"),
	PRIVATE("private");
	private String value;

	public String value() {
		return value;
	}

	ProblemVisibility(String value) {
		this.value = value;
	}

}
