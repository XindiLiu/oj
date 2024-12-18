package com.example.oj.problem;

import com.example.oj.constant.SubmissionResultType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/*
 * Information of a problem together with the current user's result on the problem.
 * Used for problem in lists.
 */
public class ProblemUserDTO {
	Long id;
	String title;
	Integer difficulty;
	SubmissionResultType userResult;
	Integer highestScore;

	public ProblemUserDTO(Problem problem) {
		this.id = problem.getId();
		this.title = problem.getTitle();
		this.difficulty = problem.getDifficulty();
		this.userResult = null;
		this.highestScore = null;
	}
}