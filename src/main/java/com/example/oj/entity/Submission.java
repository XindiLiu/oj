package com.example.oj.entity;

import com.example.oj.constant.SubmissionStatus;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
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
	SubmissionStatus status;
	@Column(name = "judgement")
	@Enumerated(EnumType.STRING)
	SubmissionResultType judgement;
	@Column(name = "run_time")
	Long runTimeMs;
	@Column(name = "memory")
	Long memoryByte;
	@Column(name = "num_passed_cases")
	Integer numPassedCases;
	@Column(name = "total_cases")
	Integer totalCases;
	@Column(name = "file_name", updatable = false)
	// The name of the file uploaded by the user.
	String fileName;
	@Column(name = "code", updatable = false)
	// The code uploaded by the user.
	String code;
	@Column(name = "message")
	// Some compile error message in case of compilation error.
	String message;
	@Column(name = "score")
	// The score of the submission. A number between 0 and 100.
	Integer score;

	public Submission() {
	}

}
