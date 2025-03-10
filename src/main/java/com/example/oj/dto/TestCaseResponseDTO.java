package com.example.oj.dto;

import com.example.oj.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
/*
 * A list of file name for all test cases of a problem. Used in the upload test case page.
 */
public class TestCaseResponseDTO {
	Integer number;
	String inputFileName;
	String outputFileName;
	Integer weight;

	public TestCaseResponseDTO(TestCase testCase) {
		this.number = testCase.getTestCaseId();
		this.inputFileName = testCase.getName() + ".in";
		this.outputFileName = testCase.getName() + ".out";
		this.weight = testCase.getWeight();
	}

}
