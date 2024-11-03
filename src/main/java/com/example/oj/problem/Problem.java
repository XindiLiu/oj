package com.example.oj.problem;

import com.example.oj.constant.ProblemVisibility;
import com.example.oj.problemDetail.ProblemDetail;
import com.example.oj.testcase.TestCase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = "testCases")
@Table(name = "problem")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
public class Problem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "title", unique = true, nullable = false, updatable = false)
	String title;
	@Column(name = "difficulty")
	Integer difficulty;
	@Enumerated(EnumType.STRING)
	ProblemVisibility visibility;
	@Column(name = "create_user")
	Long createUser;
	@CreationTimestamp
	@Column(name = "create_time")
	LocalDateTime createTime;
	@Column(name = "update_user")
	Long updateUser;
	@UpdateTimestamp
	@Column(name = "update_time")
	LocalDateTime updateTime;
	@OneToOne(mappedBy = "problem", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	@PrimaryKeyJoinColumn
	@LazyToOne(value = LazyToOneOption.NO_PROXY)
	@JsonIgnore
	ProblemDetail problemDetail;
	@OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore // Prevent serialization to avoid infinite recursion
	private List<TestCase> testCases;

}
