package com.example.oj.user;

import com.example.oj.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")

public class UserSecurity {
	/*
	 * Used in annotations for method security such as @PreAuthorize 
	 * to check if the current user is the same as the user id.
	 */
	public boolean isCurrentUser(Long userId) {
		User currentUser = SecurityUtil.getCurrentUser();
		if (currentUser == null) {
			return false;
		}
		return currentUser.getId().equals(userId);
	}
}