package com.example.oj.controller;

import com.example.oj.DTO.UserLoginDTO;
import com.example.oj.DTO.UserPasswordUpdateDTO;
import com.example.oj.DTO.UserUpdateDTO;
import com.example.oj.common.Result;
import com.example.oj.entity.User;
import com.example.oj.exception.AlreadyLoggedInException;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.projection.UserSimpleProj;
import com.example.oj.service.UserService;
import com.example.oj.utils.BeanCopyUtils;
import com.example.oj.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/user")
@Slf4j
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/login")
	public Result login(@RequestBody UserLoginDTO userLogin, HttpServletRequest request, HttpServletResponse response) throws AlreadyLoggedInException {

		log.info("login: {}", userLogin);
		String jwt = null;
		jwt = userService.login(userLogin);
//		try {
//			jwt = userService.login(userLogin);
//		} catch (AuthenticationException e) {
//			return Result.fail("Invalid username or password.");
//		} catch (AlreadyLoggedInException e) {
//			// Should not happen. Handled by front end.
//			return Result.fail("Already logged in.");
//		}
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
//		if (savedUser == null) {
//			return Result.fail("Username exist");
//		} else {
//			return Result.success(savedUser);
//		}
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
//		User currentUser;
//		if (user.getId() != null) {
//			currentUser = userService.getById(user.getId());
//			if (currentUser != null) { // Should not happen with preauthorize.
//				BeanCopyUtils.copyNonNullSrcProperties(user, currentUser);
//			} else {
//				throw new IdNotFoundException(User.class, user.getId());
//			}
//		} else {
//			return Result.fail("No user id");
//		}
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
