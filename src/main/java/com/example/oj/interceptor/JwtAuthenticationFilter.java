package com.example.oj.interceptor;

import com.example.oj.common.Result;
import com.example.oj.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
/*
 * Set the authentication token in the SecurityContext for the current user.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final UserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;
	@Autowired
	JwtUtil jwtUtil;
	@Value("${jwt.authHeader}")
	String authHeader;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwt = request.getHeader(authHeader);

		if (jwt == null || request.getRequestURI().equals("/login")) {
			filterChain.doFilter(request, response);
			return;
		}
		try {
			Long userId = jwtUtil.getUserId(jwt);
			String username = jwtUtil.getUsername(jwt);
			// Check if the user is not already authenticated
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				// Load user details from the database
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				// Validate the token and create an authentication token
				if (jwtUtil.isTokenValid(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					// Set the authentication token in the SecurityContext
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (SignatureException | MalformedJwtException e) { // Token with invalid format.
			log.error("Invalid JWT token, frontend remove the token from cookies: {}", e.getMessage());
			Result result = Result.fail("SignatureException");
			String responseBody = objectMapper.writeValueAsString(result);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(responseBody);
			return;
		}

		filterChain.doFilter(request, response);
	}
}
