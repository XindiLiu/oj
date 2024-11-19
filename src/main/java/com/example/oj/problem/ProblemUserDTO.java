package com.example.oj.problem;

import com.example.oj.constant.SubmissionResultType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemUserDTO {
	Long id;
	String title;
	Integer difficulty;
	SubmissionResultType userStatus;
	Integer highestScore;

	public ProblemUserDTO(Problem problem) {
		this.id = problem.getId();
		this.title = problem.getTitle();
		this.difficulty = problem.getDifficulty();
		this.userStatus = null;
		this.highestScore = null;
	}
}