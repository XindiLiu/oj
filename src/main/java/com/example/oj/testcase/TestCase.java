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
@ToString
@Table(name="testcase")
@DynamicUpdate
@DynamicInsert
public class TestCase {
//    @EmbeddedId
//    TestCaseId id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long testCaseId;
    @Column(name="name")
    String name;
//    @MapsId("problemId")
    @JoinColumn(name="problem_id", referencedColumnName="id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    Problem problem;
    @Column(name="input")
    String input;
    // The correct output
    @Column(name="output")
    String output;
    // The weight in the calculation of the total score.
    @Column(name="weight")
    Integer weight;
}
