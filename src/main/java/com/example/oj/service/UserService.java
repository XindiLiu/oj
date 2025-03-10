package com.example.oj.service;

import com.example.oj.dto.UserLoginDTO;
import com.example.oj.dto.UserUpdateDTO;
import com.example.oj.constant.Result;
import com.example.oj.constant.Role;
import com.example.oj.entity.User;
import com.example.oj.exception.AlreadyLoggedInException;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.repository.UserRepository;
import com.example.oj.dto.UserSimpleProj;
import com.example.oj.utils.BeanCopyUtils;
import com.example.oj.utils.JwtUtil;
import com.example.oj.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public String login(UserLoginDTO userLogin) throws AlreadyLoggedInException {
		// Check if the user is already authenticated
		if (!SecurityUtil.isGuest()) {
			log.info("User is already logged in");
			throw new AlreadyLoggedInException();
		}

		String username = userLogin.getUsername();
		String password = userLogin.getPassword();
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));
		User user = userRepository.findByUsername(username);
		String jwt = jwtUtil.generateJWT(user);
		log.info("logged in as:{}", user.getId());
		return jwt;
	}

	public void logout() {
		return;
	}

	public User getById(@PathVariable Long id) {
		User user = userRepository.getUserById(id);
		return user;
	}

	public User register(User user) {
		if (isUsernameExist(user.getUsername())) {
			return null;
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(Role.USER);
		user.setScore(Long.valueOf(0));
		User savedUser = userRepository.save(user);

		return savedUser;
	}

	public boolean isUsernameExist(String username) {
		return userRepository.existsByUsername(username);
	}

	public User update(UserUpdateDTO user) throws IdNotFoundException {
		User currentUser;
		if (user.getId() != null) {
			currentUser = this.getById(user.getId());
			if (currentUser != null) { // Should not happen with preauthorize.
				BeanCopyUtils.copyNonNullSrcProperties(user, currentUser);
			} else {
				throw new IdNotFoundException(User.class, user.getId());
			}
		} else {
			throw new IdNotFoundException(User.class, user.getId());
		}
		return userRepository.save(currentUser);
	}

	public UserSimpleProj findUserSimpleById(Long id) {
		return userRepository.findUserSimpleById(id);
	}

	public Result updatePassword(Long id, String oldPassword, String newPassword) throws IdNotFoundException {
		User user = userRepository.getUserById(id);
		if (user == null) {
			throw new IdNotFoundException(User.class, id);
		}
		String oldPasswordEncoded = user.getPassword();
		if (passwordEncoder.matches(oldPassword, oldPasswordEncoded)) {
			String newPasswordEncoded = passwordEncoder.encode(newPassword);
			int nUpdatedRows = userRepository.updatePasswordById(id, newPasswordEncoded);
			if (nUpdatedRows == 1) {
				return Result.success();
			} else {
				return Result.fail("Unknown error", 2);
			}
		} else {
			return Result.fail("Old password wrong", 1);
		}
	}

	public List rankList() {
		var list = userRepository.findAllUsersOrderByScore();
		return list;
	}
}
