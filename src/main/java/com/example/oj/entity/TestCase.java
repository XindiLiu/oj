package com.example.oj.entity;

import com.example.oj.entity.Problem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "problem") // Exclude to prevent infinite recursion
@Table(name = "testcase")
@DynamicUpdate
@DynamicInsert
/*
 * A test case is a pair of input and output used to test the correctness of a submission.
 */
public class TestCase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	Integer testCaseId;

	@Column(name = "name")
	String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_id", referencedColumnName = "id")
	@JsonIgnore
	Problem problem;

	@Column(name = "input_path")
	@JsonIgnore
	String inputPath;

	@Column(name = "output_path")
	@JsonIgnore
	String outputPath;
	@Column(name = "input")
	@Basic(fetch = FetchType.LAZY)
	@JsonIgnore
	String input;

	@Column(name = "output")
	@Basic(fetch = FetchType.LAZY)
	@JsonIgnore
	String output;
	// The weight in the calculation of the total score.
	@Column(name = "weight")
	@JsonIgnore
	Integer weight;

	public Integer getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(Integer testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Problem getProblem() {
		return problem;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}