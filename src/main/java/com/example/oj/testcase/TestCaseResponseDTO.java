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
		String inputFileName = Paths.get(testCase.getInputPath()).getFileName().toString();
		String outputFileName = Paths.get(testCase.getOutputPath()).getFileName().toString();
		this.number = testCase.testCaseId;
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
		this.weight = testCase.getWeight();
	}

}
