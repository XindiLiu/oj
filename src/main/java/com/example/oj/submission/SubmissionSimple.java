package com.example.oj.submission;

import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
import com.example.oj.problem.ProblemSimple;
import com.example.oj.problem.ProblemSimplePorj;
import com.example.oj.user.UserSimple;
import com.example.oj.user.UserSimpleProj;
import lombok.Data;
import lombok.ToString;

import java.util.Calendar;

@Data
@ToString
public class SubmissionSimple implements SubmissionSimpleProj {
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


	public SubmissionSimple(Long id, Long userId, Long problemId, Calendar createTime, ProgrammingLanguage language, SubmissionStatus status, SubmissionResultType judgement, Long runTimeMs, Long memoryByte, Integer numPassedCases, Integer totalCases, Integer score) {
		this.id = id;
		this.problem = new ProblemSimple(problemId);
		this.user = new UserSimple(userId);
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

	public SubmissionSimple() {
	}

}
