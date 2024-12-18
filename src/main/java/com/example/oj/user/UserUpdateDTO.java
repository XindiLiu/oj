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
public class UserUpdateDTO implements Serializable {
	public Long id;
	public String displayName;
//    public Long score;
}
