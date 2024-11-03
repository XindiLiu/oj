// package com.example.oj.submission;

// import com.example.oj.constant.SubmissionResultType;
// import com.example.oj.problem.Problem;
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.ToString;
// import org.hibernate.annotations.DynamicInsert;
// import org.hibernate.annotations.DynamicUpdate;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @ToString
// @Entity
// @DynamicUpdate
// @DynamicInsert
// // TODO: Add percent/number of passed testcasese.
// // TODO: submissionResultType, runTime, memory for each testcas.
// public class SubmissionResult {
// 	@Column(name = "judgement")
// 	@Enumerated(EnumType.STRING)
// 	SubmissionResultType submissionResultType;
// 	@Column(name = "run_time")
// 	Long runTime;
// 	@Column(name = "memory")
// 	Long memory;
// 	@Column(name = "message")
// 	String message; // Compile error if compilation failed
// 	@Column(name = "num_passed_cases")
// 	Integer numPassedCases;
// 	@Column(name = "total_cases")
// 	Integer totalCases;
// }
