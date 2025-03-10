package com.example.oj.dto;

import com.example.oj.constant.ProblemVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSearchDTO {

	public String title;
	public Long createUser;
	public Integer minDifficulty;
	public Integer maxDifficulty;
	public ProblemVisibility visibility;
	public Long filterUserId; // The ID of the user to whom includeAll, includePassed, includeFailed, and includeNotTried apply.
	public Boolean includeAll; // Include all problems, if true the other includeXXX will be ignored.
	public Boolean includePassed; // Include problems that the user has passed (accepted).
	public Boolean includeFailed; // Include problems that the user has tried but not passed.
	public Boolean includeNotTried; // Include problems that the user has not tried.
}
