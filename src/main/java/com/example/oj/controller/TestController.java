package com.example.oj.controller;

import com.example.oj.common.Result;
import com.example.oj.entity.TestEntity;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
@Slf4j
public class TestController {
	@GetMapping("/hello")
	public String hello() {
		log.info("hello");
		return "hello spring security";
	}

	@PostMapping
	@ResponseBody
	public Result test(@RequestBody TestEntity test) {
		log.info("test {}", test);
		return Result.success(test);
	}

	@GetMapping
	public Result test(HttpServletRequest request, HttpServletResponse response) {
		log.info("test {} {}", request, response);

		log.info("Host: {}", request.getHeader("Host"));
		Cookie tokenCookie = new Cookie("token", "12345");
		tokenCookie.setMaxAge(Integer.MAX_VALUE);
		response.addCookie(tokenCookie);
		return Result.success();
	}

	@PostMapping("/cookie")
	public Result addCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie tokenCookie = new Cookie("token", "12345");
		tokenCookie.setMaxAge(Integer.MAX_VALUE);
		response.addCookie(tokenCookie);
		return Result.success();
	}

	@GetMapping("/cookie")
	public Result getCookie(HttpServletRequest request, HttpServletResponse response) {
		var cookies = request.getCookies();
		for (var c : cookies) {
			log.info("cookie: {}", c.getName());
		}

		return Result.success();
	}

	@GetMapping("/{id}")
	public Result testGet(@PathVariable int id) {
		log.info("testGet {}", id);
		return Result.success(id);
	}
}
