package com.example.oj.utils;

import com.example.oj.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

	private final Key key;
	private final Long jwtExpire;

	public JwtUtil(@Value("${jwt.secret}") String secret,
				   @Value("${jwt.expiration}") Long jwtExpire) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.jwtExpire = jwtExpire;
	}

	/**
	 * Generate JWT
	 */
	public String generateJWT(User user) {
		log.info("Generating JWT at {}", System.currentTimeMillis());
		String jwt = Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.setHeaderParam("alg", "HS256")
				.setSubject("user")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpire))
				.claim("id", user.getId())
				.claim("username", user.getUsername())
				.signWith(key)
				.compact();
		return jwt;
	}

	/**
	 * Extract token from the request cookies
	 */
	public String getToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return null;
		}
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("token")) {
				return cookie.getValue();
			}
		}
		return null;
	}

	/**
	 * Get claims from the token
	 */
	public Claims getClaims(String token) {
		Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claims;
	}

	/**
	 * Get user ID from the token
	 */
	public Long getUserId(String token) {
		Claims claims = getClaims(token);
		Integer idInt = claims.get("id", Integer.class);
		return idInt != null ? idInt.longValue() : null;
	}

	/**
	 * Get username from the token
	 */
	public String getUsername(String token) {
		Claims claims = getClaims(token);
		return claims.get("username", String.class);
	}

	/**
	 * Get token expiration time
	 */
	public Date getExpireTime(String token) {
		Claims claims = getClaims(token);
		return claims.getExpiration();
	}

	/**
	 * Check if the token is not expired
	 *
	 * @param token the JWT token
	 * @return true if not expired, false otherwise
	 */
	public boolean isTokenNotExpired(String token) {
		return new Date().before(getExpireTime(token));
	}

	/**
	 * Validate the token against the user details
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		if (userDetails == null) return false;
		final String username = getUsername(token);
		return (username.equals(userDetails.getUsername())) && isTokenNotExpired(token);
	}

	/**
	 * Get user ID from the request
	 */
	public Long getUserId(HttpServletRequest request) {
		try {
			String token = getToken(request);
			return getUserId(token);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
