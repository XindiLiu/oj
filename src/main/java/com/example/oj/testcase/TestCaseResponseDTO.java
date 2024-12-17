package com.example.oj.testcase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.nio.file.Paths;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TestCaseResponseDTO {
	Integer number;
	String inputFileName;
	String outputFileName;
	Integer weight;

	public TestCaseResponseDTO(TestCase testCase) {
		this.number = testCase.testCaseId;
		this.inputFileName = testCase.getName() + ".in";
		this.outputFileName = testCase.getName() + ".out";
		this.weight = testCase.getWeight();
	}

}
