package com.example.oj.userProblem;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.submission.Submission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import com.example.oj.problem.Problem;
import com.example.oj.user.User;
import jakarta.persistence.*;

import java.io.Serializable;


@Entity
@Table(name = "user_problem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProblem {
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
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	UserProblem.UserProblemId id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "submission_id", referencedColumnName = "id", nullable = false, unique = true)
	@JsonIgnore
	Submission bestSubmission;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	SubmissionResultType status;

	@Column(name = "highest_score", nullable = false)
	Integer highestScore;

	// Used at the first submission of this user on this problem
	public UserProblem(User user, Problem problem, Submission submission) {
		this.id = new UserProblem.UserProblemId(user, problem);
		this.bestSubmission = submission;
		this.status = submission.getJudgement();
		this.highestScore = submission.getScore();
	}


}
