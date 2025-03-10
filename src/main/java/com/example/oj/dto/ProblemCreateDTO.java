package com.example.oj.dto;

import com.example.oj.constant.ProblemVisibility;
import com.example.oj.entity.ProblemDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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
	List<ProblemDetail.SamplePair> sampleIo;

}
