package com.example.oj.testcase;

import com.example.oj.problem.Problem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "problem") // Exclude to prevent infinite recursion
@Table(name = "testcase")
@DynamicUpdate
@DynamicInsert
public class TestCase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer testCaseId;

	@Column(name = "name")
	String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_id", referencedColumnName = "id")
	@JsonIgnore
	Problem problem;

	@Column(name = "input_path")
	String inputPath;

	@Column(name = "output_path")
	String outputPath;

	// The weight in the calculation of the total score.
	@Column(name = "weight")
	Integer weight;
}