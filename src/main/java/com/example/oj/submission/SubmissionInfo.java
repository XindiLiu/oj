package com.example.oj.submission;

import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;

import java.util.Calendar;

/**
 * Projection for {@link Submission}
 */
public interface SubmissionInfo {
	Long getId();

	Calendar getCreateTime();

	ProgrammingLanguage getLanguage();

	SubmissionStatus getStatus();

	SubmissionResultType getJudgement();

	Long getRunTimeMs();

	Long getMemoryByte();

	Integer getNumPassedCases();

	Integer getTotalCases();

	String getFileName();

	String getCode();

	String getMessage();

	Integer getScore();

	ProblemInfo getProblem();

	UserInfo getUser();

	/**
	 * Projection for {@link com.example.oj.problem.Problem}
	 */
	interface ProblemInfo {
		Long getId();

		String getTitle();
	}

	/**
	 * Projection for {@link com.example.oj.user.User}
	 */
	interface UserInfo {
		Long getId();

		String getUsername();

		String getName();
	}
}