package com.example.oj.problem;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.userProblem.UserProblem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemUserDTO {
	Long problemId;
	String title;
	Integer difficulty;
	SubmissionResultType userStatus;
	Integer highestScore;
}