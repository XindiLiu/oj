package com.example.oj.entity;

import com.example.oj.constant.SubmissionResultType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "user_problem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/*
 * This entity represents the relationship between a User and a Problem,
 * capturing the result of the user's submissions for that specific problem.
 */
public class UserProblemResult {
	@Embeddable
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserProblemId implements Serializable {
		@ManyToOne(fetch = FetchType.LAZY, optional = false)
		@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
		@JsonIgnore
		User user;
		@ManyToOne(fetch = FetchType.LAZY, optional = false)
		@JoinColumn(name = "problem_id", referencedColumnName = "id", nullable = false, updatable = false)
		@JsonIgnore
		Problem problem;
	}

	@Id
	@JsonIgnore
	UserProblemResult.UserProblemId id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "submission_id", referencedColumnName = "id", nullable = false, unique = true)
	@JsonIgnore
	Submission bestSubmission;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	SubmissionResultType status;

	@Column(name = "highest_score", nullable = false)
	Integer highestScore;

	/*
	 * Used in response.
	 */
	@Transient
	Long submissionId;

	// Used at the first submission of this user on this problem
	public UserProblemResult(User user, Problem problem, Submission submission) {
		this.id = new UserProblemResult.UserProblemId(user, problem);
		this.bestSubmission = submission;
		this.status = submission.getJudgement();
		this.highestScore = submission.getScore();
	}

}
