package com.example.oj.problemDetail;

import com.example.oj.problem.Problem;
import com.example.oj.testcase.TestCase;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.core.io.Resource;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name="problem_detail")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@DynamicUpdate // Use dynamic update. Otherwise not changed columns will be overwritten.
@DynamicInsert // Use dynamic update. Otherwise column default value won't work.
public class ProblemDetail {
    @Id
    Long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    Problem problem;
    @Column(name="description")
    String description;
    @Column(name="input_format")
    String inputFormat;
    @Column(name="output_format")
    String outputFormat;
    @Column(name="sample_data")
    String sampleData;
    @Column(name="time_limit", columnDefinition = "integer default 1")
    Integer timeLimitSeconds;
    @Column(name="memory_limit", columnDefinition = "integer default 1024")
    Integer memoryLimitMB;

}