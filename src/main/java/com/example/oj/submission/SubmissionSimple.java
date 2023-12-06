package com.example.oj.submission;

import com.example.oj.problem.Problem;
import com.example.oj.problem.ProblemSimplest;
import com.example.oj.user.User;
import com.example.oj.user.UserSimplest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubmissionSimple {
    Long id;
    ProblemSimplest problem;
    UserSimplest user;
    Calendar createTime;
    String judgement;
    Double runTime;
    Double memory;
    String language;
}
