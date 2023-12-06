package com.example.oj.submission;

import com.example.oj.submission.Submission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class SubmissionDetail {
    @Id
    Long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    Submission submission;
    @Column(updatable = false)
    String code;
    String message;
}
