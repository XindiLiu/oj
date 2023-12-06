package com.example.oj.submission;
import com.example.oj.constant.JudgementConverter;
import com.example.oj.user.User;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.Judgement;
import com.example.oj.problem.Problem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name="submission")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @JoinColumn(name="problem_id", referencedColumnName="id", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    Problem problem;
    @JoinColumn(name="user_id", referencedColumnName="id", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    User user;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    Calendar createTime;

//    @Enumerated(EnumType.ORDINAL)
//    @Convert(converter = JudgementConverter.class)
    @Column(name = "judgement")
    @Enumerated(EnumType.STRING)
    Judgement judgement;
    @Column(name = "run_time")
    Double runTime;
    @Column(name = "memory")
    Double memory;
    @Column(name = "language", updatable = false)
    @Enumerated(EnumType.STRING)
    ProgrammingLanguage language;
    @Column(name = "message")
    String message;
    @Column(name = "code", updatable = false)
    String code;
    @Column(name = "file_name", updatable = false)
    String fileName;

}
