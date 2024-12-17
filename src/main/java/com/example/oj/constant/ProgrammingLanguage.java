package com.example.oj.constant;

public enum ProgrammingLanguage {
	CPP("cpp"),
	C("c"),
	JAVA("java"),
	PYTHON("py");
	public final String fileExtension;

	private ProgrammingLanguage(String fileExtension) {
		this.fileExtension = fileExtension;
	}
}
