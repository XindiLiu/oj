package com.example.oj.submission;

import com.example.oj.user.User;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
import com.example.oj.submission.SubmissionStatus;
import com.example.oj.problem.Problem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Calendar;

@Data
// Default value does not work with NoArgsConstructor. Create explicit constructor instead.
//@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "submission")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Submission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@JoinColumn(name = "problem_id", referencedColumnName = "id", nullable = false, updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	Problem problem;
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	User user;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", updatable = false)
	Calendar createTime;
	@Column(name = "language", updatable = false)
	@Enumerated(EnumType.STRING)
	ProgrammingLanguage language;
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	SubmissionStatus submissionStatus;
	@Column(name = "judgement")
	@Enumerated(EnumType.STRING)
	SubmissionResultType submissionResultType;
	@Column(name = "file_name", updatable = false)
	String fileName;
	@Column(name = "run_time")
	Long runTime;
	@Column(name = "memory")
	Long memory;
	@Column(name = "message")
	String message;
	@Column(name = "code", updatable = false)
	String code;

	@Column(name = "num_passed_cases")
	Integer numPassedCases;
	@Column(name = "total_cases")
	Integer totalCases;

	public Submission() {
	}

}
