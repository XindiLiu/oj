package com.example.oj.common;

import lombok.Data;

import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;

@Data
//@ResponseBody
public class Result<T> implements Serializable {
	int code;
	String msg;
	T data;

	public Result(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public static <T> Result<T> success(T data) {
		return new Result(1, null, data);
	}

	public static <T> Result<T> success() {
		return new Result(1, null, null);
	}

	public static <T> Result<T> fail(String msg) {
		return new Result(0, msg, null);
	}

	public static <T> Result<T> fail(T data, String msg) {
		return new Result(0, msg, data);
	}

	@Override
	public String toString() {
		return "Result{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
