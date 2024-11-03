package com.example.oj.interceptor;

import com.example.oj.constant.SecurityConstants;
import com.example.oj.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailsService;

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
		// Extract username from token
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
		filterChain.doFilter(request, response);
	}
}
