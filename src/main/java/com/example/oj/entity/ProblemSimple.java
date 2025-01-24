package com.example.oj.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProblemSimple {
	@Id
	Long id;
	String title;

	public ProblemSimple(Long id) {
		this.id = id;
	}
}
