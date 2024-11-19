package com.example.oj.constant;

public enum Role {
	ADMIN,
	MEMBER,
	USER,
	GUEST;
	
	public String role() {
		return "ROLE_" + this.name();
	}
}
