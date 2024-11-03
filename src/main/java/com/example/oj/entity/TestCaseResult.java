package com.example.oj.entity;

import com.example.oj.constant.SubmissionResultType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class TestCaseResult {

	Integer caseNumber;
	String output;
	SubmissionResultType status;
	Double runTime;
	Double memoryUsage;

}
