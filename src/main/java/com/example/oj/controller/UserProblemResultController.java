package com.example.oj.controller;

import com.example.oj.constant.Result;
import com.example.oj.entity.UserProblemResult;
import com.example.oj.service.UserProblemResultService;
import com.example.oj.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserProblemResultController {

	private final UserProblemResultService userProblemService;

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
