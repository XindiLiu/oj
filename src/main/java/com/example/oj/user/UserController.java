package com.example.oj.user;

import com.example.oj.common.Result;
import com.example.oj.exception.AlreadyLoggedInException;
import com.example.oj.exception.IdNotFoundException;
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
	@Autowired
	UserService userService;

	@PostMapping("/login")
	public Result login(@RequestBody UserLoginDTO userLogin, HttpServletRequest request, HttpServletResponse response) {

		log.info("login: {}", userLogin);
		//		userLogin.setPassword(MD5Utils.md5(userLogin.getPassword()));
		String jwt = null;
		try {
			jwt = userService.login(userLogin);
		} catch (AuthenticationException e) {
			return Result.fail("Invalid username or password.");
		} catch (AlreadyLoggedInException e) {
			// Should not happen. Handled by front end.
			return Result.fail("Already logged in.");
		}
		//		if (result.getCode() == 1) {
		//			User user = (User) result.getData();
		//			Cookie tokenCookie = new Cookie("token", JwtUtil.generateJWT(user));
		//			tokenCookie.setMaxAge(Integer.MAX_VALUE);
		//			response.addCookie(tokenCookie);
		//			log.info("logged in as:{}", user.getId());
		//		} else {
		//			log.info("login failed");
		//		}
		return Result.success(jwt);
	}

	@PostMapping("/logout")
	public Result logout() {
		return Result.success();
	}

	@PostMapping("/register")
	public Result register(@RequestBody UserLoginDTO userRegister) {
		User user = new User();
		BeanUtils.copyProperties(userRegister, user);
		User savedUser = userService.register(user);
		if (savedUser == null) {
			return Result.fail("Username exist");
		} else {
			return Result.success(savedUser);
		}
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

	@PostMapping("/userUpdate")
	@PreAuthorize("@userSecurity.isCurrentUser(#user.id)")
	public Result update(@RequestBody UserUpdateDTO user) throws IdNotFoundException {
		log.info("Update user info: {}", user);
		User currentUser;
		if (user.getId() != null) {
			currentUser = userService.getById(user.getId());
			if (currentUser != null) { // Should not happen with preauthorize.
				BeanCopyUtils.copyNonNullSrcProperties(user, currentUser);
			} else {
				throw new IdNotFoundException(User.class, user.getId());
			}
		} else {
			return Result.fail("No user id");
		}
		userService.update(currentUser);
		return Result.success();
	}

	@PostMapping("/userUpdatePassword")
	@PreAuthorize("@userSecurity.isCurrentUser(#passwordDto.id)")
	public Result updatePassword(@RequestBody UserPasswordUpdateDTO passwordDto) throws IdNotFoundException {
		log.info("Update password: {}", passwordDto);
		return userService.updatePassword(passwordDto.id, passwordDto.oldPassword, passwordDto.newPassword);
	}

	@GetMapping("/currentUser")
	public Result currentUser() {
		return Result.success(SecurityUtil.getCurrentUser());
	}

	@GetMapping("/user/rank")
	public Result rankList() {
		var ranklist = userService.rankList();
		return Result.success(ranklist);
	}

}
