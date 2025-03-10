package com.example.oj.utils;

import com.example.oj.entity.User;
import com.example.oj.utils.SecurityUtil;
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