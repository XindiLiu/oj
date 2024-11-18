package com.example.oj.problem;

import com.example.oj.constant.ProblemVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSearchDTO {

	public String title;
	public Long createUser;
	public Integer minDifficulty;
	public Integer maxDifficulty;
	public ProblemVisibility visibility;
	public Boolean includeAll;
	public Boolean includePassed;
	public Boolean includeFailed;
	public Boolean includeNotTried;
//	public ProblemSearchDTO() {
//	}
//
//	public ProblemSearchDTO(String title, Long createUser, Integer minDifficulty, Integer maxDifficulty, ProblemVisibility visibility) {
//		this.title = title;
//		this.createUser = createUser;
//		this.minDifficulty = minDifficulty;
//		this.maxDifficulty = maxDifficulty;
//		this.visibility = visibility;
//	}
}
