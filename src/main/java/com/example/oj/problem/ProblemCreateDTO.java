package com.example.oj.problem;

import com.example.oj.constant.ProblemVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProblemCreateDTO {
	String title;
	Integer difficulty;
	ProblemVisibility visibility;
	String description;
	String inputFormat;
	String outputFormat;
	String sampleData;
	Integer timeLimitSeconds;
	Integer memoryLimitMB;

}
