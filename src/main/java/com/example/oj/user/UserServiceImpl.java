package com.example.oj.user;

import com.example.oj.common.Result;
//import com.example.oj.utils.JwtUtil;
import com.example.oj.constant.Role;
import com.example.oj.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
public class UserServiceImpl {
	@Autowired
	UserRepository userRepository;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	PasswordEncoder passwordEncoder;

	public String login(UserLoginDTO userLogin) {
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
}
