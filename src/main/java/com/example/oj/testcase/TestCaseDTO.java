package com.example.oj.testcase;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TestCaseDTO {
    Long problemId;
    String name;
    String input;
    String output;
    Integer weight;
}
