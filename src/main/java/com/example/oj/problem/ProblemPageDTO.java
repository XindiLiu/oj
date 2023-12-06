package com.example.oj.problem;

import com.example.oj.constant.ProblemVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemPageDTO {
    public String title;
    public Long createUser;
    public Integer difficultyLowerBound;
    public Integer difficultyUpperBound;
    public ProblemVisibility visibility;

}
