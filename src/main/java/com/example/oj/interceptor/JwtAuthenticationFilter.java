package com.example.oj.interceptor;

import com.example.oj.common.Result;
import com.example.oj.constant.SecurityConstants;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.swing.table.TableRowSorter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		//		if (request.getServletPath().contains("login")) {
		//			filterChain.doFilter(request, response);
		//			return;
		//		}

		// Extract token from request
		String authHeader = request.getHeader(SecurityConstants.AUTH_HEADER);

		if (authHeader == null) {
			filterChain.doFilter(request, response);
			return;
		}
		String jwt = authHeader;
		try {
			Long userId = JwtUtil.getUserId(jwt);
			String username = JwtUtil.getUsername(jwt);
			// Check if the user is not already authenticated
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				// Load user details from the database
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				// Validate the token and create an authentication token
				if (JwtUtil.isTokenValid(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					// Set the authentication token in the SecurityContext
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (SignatureException | MalformedJwtException e) {
			log.error("Invalid JWT token, frontend remove the token from cookies: {}", e.getMessage());
			Result result = Result.fail("SignatureException");
			String responseBody = objectMapper.writeValueAsString(result);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(responseBody);
			return;
		}

		// Extract username from token

		filterChain.doFilter(request, response);
	}

	// Caused by JwtUtil.getClaims(), when the JWT token is invalid

}
