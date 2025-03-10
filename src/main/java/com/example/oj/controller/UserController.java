package com.example.oj.controller;

import com.example.oj.dto.UserLoginDTO;
import com.example.oj.dto.UserPasswordUpdateDTO;
import com.example.oj.dto.UserUpdateDTO;
import com.example.oj.constant.Result;
import com.example.oj.entity.User;
import com.example.oj.exception.AlreadyLoggedInException;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.dto.UserSimpleProj;
import com.example.oj.service.UserService;
import com.example.oj.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// TODO: spring security oauth2
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/login")
	public Result login(@RequestBody UserLoginDTO userLogin) throws AlreadyLoggedInException {

		log.info("login: {}", userLogin);
		String jwt = null;
		jwt = userService.login(userLogin);
		return Result.success(jwt);
	}

	// Handled by front end.
	@PostMapping("/logout")
	public Result logout() {
		return Result.success();
	}

	@PostMapping("/register")
	public Result register(@RequestBody UserLoginDTO userRegister) {
		User user = new User();
		BeanUtils.copyProperties(userRegister, user);
		User savedUser = userService.register(user);
		return Result.success(savedUser);
	}

	@GetMapping("/user/{id}")
	public Result getById(@PathVariable Long id) {
		User user = userService.getById(id);
		return Result.success(user);
	}

	@GetMapping("/user/simple/{id}")
	public Result findUserSimpleById(@PathVariable Long id) {
		UserSimpleProj user = userService.findUserSimpleById(id);
		return Result.success(user);
	}

	/*
	 * Update user profile.
	 */
	@PostMapping("/userUpdate")
	@PreAuthorize("@userSecurity.isCurrentUser(#user.id)")
	public Result update(@RequestBody UserUpdateDTO user) throws IdNotFoundException {
		log.info("Update user info: {}", user);
		userService.update(user);
		return Result.success();
	}

	@PostMapping("/userUpdatePassword")
	@PreAuthorize("@userSecurity.isCurrentUser(#passwordDto.id)")
	public Result updatePassword(@RequestBody UserPasswordUpdateDTO passwordDto) throws IdNotFoundException {
		log.info("Update password: {}", passwordDto);
		return userService.updatePassword(passwordDto.id, passwordDto.oldPassword, passwordDto.newPassword);
	}

	/*
	 * Get current user by the token in the request header.
	 */
	@GetMapping("/currentUser")
	public Result currentUser() {
		return Result.success(SecurityUtil.getCurrentUser());
	}

	/*
	 * Rank all users by score.
	 * TODO: Add limit
	 */
	@GetMapping("/user/rank")
	public Result rankList() {
		var ranklist = userService.rankList();
		return Result.success(ranklist);
	}

}
