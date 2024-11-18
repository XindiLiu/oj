package com.example.oj.user;

import com.example.oj.common.Result;
//import com.example.oj.utils.JwtUtil;
import com.example.oj.constant.Role;
import com.example.oj.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@Slf4j
public class UserService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	PasswordEncoder passwordEncoder;

	public String login(UserLoginDTO userLogin) {
		// Check if the user is already authenticated
		if (SecurityContextHolder.getContext().getAuthentication() != null &&
				SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			log.info("User is already logged in");
			return null;
		}

		String username = userLogin.getUsername();
		String password = userLogin.getPassword();
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));
		User user = userRepository.findByUsername(username);
		String jwt = JwtUtil.generateJWT(user);
		log.info("logged in as:{}", user.getId());
		return jwt;

	}

	public void logout() {
		return;
	}

	public Result save(User user) {

		User savedUser = null;
		savedUser = userRepository.save(user);
		if (savedUser == null) {
			return Result.fail("Registration failed");
		} else {
			//            user.setPassword("******");
			return Result.success(savedUser);
		}
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

	public void update(User user) {
		userRepository.save(user);
	}

	public UserSimpleProj findUserSimpleById(Long id) {
		return userRepository.findUserSimpleById(id);
	}
}
