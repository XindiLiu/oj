package com.example.oj.entity;

import com.example.oj.constant.ProblemVisibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Calendar;
import java.util.List;

// TODO: 看lombok annotation
// TODO: controller 不出现 entity， 只和service交互, dto在service里转换成entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "problem")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class Problem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "title")
	String title;
	@Column(name = "difficulty")
	Integer difficulty;
	@Enumerated(EnumType.STRING)
	ProblemVisibility visibility; // TODO: Handle visibility
	@Column(name = "create_user")
	Long createUser;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	Calendar createTime;
	@Column(name = "update_user")
	Long updateUser;
	@UpdateTimestamp
	@Column(name = "update_time")
	@Temporal(TemporalType.TIMESTAMP)
	Calendar updateTime;
	@OneToOne(mappedBy = "problem", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	@PrimaryKeyJoinColumn
	@JsonIgnore
	ProblemDetail problemDetail;
	@OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<TestCase> testCases;

	@OneToMany(mappedBy = "id.problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<UserProblemResult> userProblemResults;

	public static Problem emptyProblem() {
		Problem problem = new Problem();
		problem.setProblemDetail(new ProblemDetail());
		problem.getProblemDetail().setProblem(problem);
		return problem;
	}

}
