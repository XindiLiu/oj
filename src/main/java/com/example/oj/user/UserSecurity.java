package com.example.oj.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

	public boolean isCurrentUser(Long userId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}
		// Retrieve the current user's ID
		User currentUser = (User) authentication.getPrincipal();
		return currentUser.getId().equals(userId);
	}
}