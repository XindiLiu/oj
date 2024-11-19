package com.example.oj.utils;

import com.example.oj.constant.Role;
import com.example.oj.user.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
	public static User getCurrentUser() {
		// Not logged in
		if (isGuest()) {
//			if (SecurityContextHolder.getContext().getAuthentication() == null) {
			return null;
		}
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user;
	}

	public static boolean isGuest() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(Role.GUEST.role()));
	}
}
