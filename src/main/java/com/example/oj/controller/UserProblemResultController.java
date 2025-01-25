package com.example.oj.controller;

import com.example.oj.common.Result;
import com.example.oj.entity.UserProblemResult;
import com.example.oj.service.UserProblemResultService;
import com.example.oj.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserProblemResultController {

	private final UserProblemResultService userProblemService;

	@Autowired
	public UserProblemResultController(UserProblemResultService userProblemService) {
		this.userProblemService = userProblemService;
	}

	/*
	 * Get the best submission result of the current user for a problem.
	 */
	@GetMapping("/userproblem")
	public Result get(@RequestParam Long problemId) {
		if (SecurityUtil.isGuest()) {
			return null;
		}

		UserProblemResult bestSubmission = userProblemService.getUserProblem(problemId);
		return Result.success(bestSubmission);
	}
}
