package com.example.oj.userProblem;

import com.example.oj.common.Result;
import com.example.oj.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserProblemResultController {
	@Autowired
	UserProblemResultService userProblemService;

	/*
	 * Get the best submission result of the current user for a problem.
	 */
	@GetMapping("/userproblem")
	public Result get(@RequestParam Long problemId) {
		if (SecurityUtil.isGuest()) {
			return null;
		}
		Long userId = SecurityUtil.getCurrentUser().getId();
		var optionalResult = userProblemService.getUserProblem(userId, problemId);
		if (optionalResult.isPresent()) {
			UserProblemResult bestSubmission = optionalResult.get();
			bestSubmission.setSubmissionId(bestSubmission.getBestSubmission().getId());
			return Result.success(bestSubmission);
		} else {
			return Result.success(null);
		}
	}
}
