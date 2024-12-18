package com.example.oj.utils;

import com.example.oj.constant.Role;
import com.example.oj.user.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
	public static User getCurrentUser() {
		// If not logged in, the principal is not a User object and cannot be casted to User.
		if (isGuest()) {
			return null;
		}
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user;
	}

	public static boolean isGuest() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals(Role.GUEST.role()));
	}
}
