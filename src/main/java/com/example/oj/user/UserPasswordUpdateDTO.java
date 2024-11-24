package com.example.oj.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserPasswordUpdateDTO implements Serializable {
	public Long id;
	public String oldPassword;
	public String newPassword;
//    public Long score;
}
