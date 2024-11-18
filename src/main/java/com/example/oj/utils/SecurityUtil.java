package com.example.oj.utils;

import com.example.oj.user.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
	public static User getCurrentUser() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user;
	}


}
