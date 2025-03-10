package com.example.oj.constant;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
//@ResponseBody
public class Result<T> implements Serializable {
	boolean success;
	int code;
	String msg;
	T data;

	public static <T> Result<T> success(T data) {
		return new Result(true, 0, null, data);
	}

	public static <T> Result<T> success() {
		return new Result(true, 0, null, null);
	}

	public static <T> Result<T> fail() {
		return new Result(false, 1, null, null);
	}

	public static <T> Result<T> fail(String msg, int code) {
		return new Result(false, 1, null, null);
	}

	public static <T> Result<T> fail(String msg) {
		return new Result(false, 1, msg, null);
	}

	public static <T> Result<T> fail(T data, String msg) {
		return new Result(false, 1, msg, data);
	}

	public static <T> Result<T> fail(T data, String msg, int code) {
		return new Result(false, code, msg, data);
	}

}
