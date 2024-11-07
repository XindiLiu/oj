package com.example.oj.test;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.Exception;

import com.example.oj.common.Result;

@RestController
@RequestMapping("/error")
public class ExceptionController {
	@PostMapping
	public void throwException(@RequestBody String e) throws Exception {
		e = e.trim();
		if (e.equals("Exception")) {
			throw new Exception(String.format("Throwed %s", e));
		}
		if (e.equals("IOException")) {
			throw new IOException(String.format("Throwed %s", e));
		}
		if (e.equals("RuntimeException")) {
			throw new RuntimeException(String.format("Throwed %s", e));
		}
		if (e.equals("AccessDeniedException")) {
			throw new AccessDeniedException(String.format("Throwed %s", e));
		}
	}
}