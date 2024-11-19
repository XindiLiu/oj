package com.example.oj.submission;

import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;

import java.util.Calendar;

public interface SubmissionSimpleProj {
	Long getId();
//
//	ProblemSimple getProblem();
//
//	UserSimple getUser();

	Calendar getCreateTime();

	ProgrammingLanguage getLanguage();

	SubmissionStatus getStatus();

	SubmissionResultType getJudgement();

	Long getRunTimeMs();

	Long getMemoryByte();

	Integer getNumPassedCases();

	Integer getTotalCases();

	Integer getScore();
}