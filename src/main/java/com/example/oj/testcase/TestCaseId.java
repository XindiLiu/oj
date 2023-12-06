package com.example.oj.testcase;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TestCaseId implements Serializable {
     Long problemId;
     @GeneratedValue(strategy = GenerationType.AUTO)
     Long testCaseId;
}
