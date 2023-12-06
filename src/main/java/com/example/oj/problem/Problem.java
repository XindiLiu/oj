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
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name="problem")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(mappedBy = "problem",cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    @LazyToOne(value = LazyToOneOption.NO_PROXY)
    @JsonIgnore
    ProblemDetail problemDetail;
    @Column(name="title", unique = true, nullable = false, updatable = false)
    String title;
    @Column(name="difficulty")
    Integer difficulty;
    @Enumerated(EnumType.STRING)
    ProblemVisibility visibility;
    @Column(name="create_user")
    Long createUser;
    @Column(name="create_time")
    LocalDateTime createTime;
    @Column(name="update_user")
    Long updateUser;
    @Column(name="update_time")
    LocalDateTime updateTime;
//    @OneToMany(mappedBy="problem", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//    Set<TestCase> testCases;
}
