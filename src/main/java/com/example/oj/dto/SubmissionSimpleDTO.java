package com.example.oj.dto;

import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
import com.example.oj.constant.SubmissionStatus;
import lombok.Data;
import lombok.ToString;

import java.util.Calendar;

@Data
@ToString
/**
 * Simple information of a Submission displayed in submission lists.
 */
public class SubmissionSimpleDTO {
	Long id;
	ProblemSimple problem;
	UserSimple user;
	Calendar createTime;
	ProgrammingLanguage language;
	SubmissionStatus status;
	SubmissionResultType judgement;
	Long runTimeMs;
	Long memoryByte;
	Integer numPassedCases;

	Integer totalCases;
	Integer score;

	public SubmissionSimpleDTO(Long id, Long userId, String userName, Long problemId, String problemTitle,
							   Calendar createTime, ProgrammingLanguage language, SubmissionStatus status, SubmissionResultType judgement,
							   Long runTimeMs, Long memoryByte, Integer numPassedCases, Integer totalCases, Integer score) {
		this.id = id;
		this.problem = new ProblemSimple(problemId, problemTitle);
		this.user = new UserSimple(userId, userName);
		this.createTime = createTime;
		this.language = language;
		this.status = status;
		this.judgement = judgement;
		this.runTimeMs = runTimeMs;
		this.memoryByte = memoryByte;
		this.numPassedCases = numPassedCases;
		this.totalCases = totalCases;
		this.score = score;
	}

	public SubmissionSimpleDTO() {
	}

}
