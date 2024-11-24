package com.example.oj.userProblem;

import com.example.oj.common.Result;
import com.example.oj.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserProblemController {
	@Autowired
	UserProblemService userProblemService;

	@GetMapping("/userproblem")
	public Result get(@RequestParam Long problemId) {
		if (SecurityUtil.isGuest()) {
			return null;
		}
		Long userId = SecurityUtil.getCurrentUser().getId();
		var optianlResult = userProblemService.getUserProblem(userId, problemId);
		if (optianlResult.isPresent()) {
			return Result.success(optianlResult.get());
		} else {
			return Result.success(null);
		}
	}
}
