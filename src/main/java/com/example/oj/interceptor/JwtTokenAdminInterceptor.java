package com.example.oj.interceptor;

import com.example.oj.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT token validation interceptor. Not used.
 */
@Component
@Slf4j
//@CrossOrigin
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

	private final JwtUtil jwtUtil;

	public JwtTokenAdminInterceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	/**
	 * Validate JWT
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;
	}

}
