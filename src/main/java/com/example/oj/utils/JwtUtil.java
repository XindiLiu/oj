package com.example.oj.utils;

import com.example.oj.constant.SecurityConstants;
import com.example.oj.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {

	private final static String keyBytes = "00000000000000000000000000000000";
	private final static Key key = Keys.hmacShaKeyFor(keyBytes.getBytes());

	//	/**
//	 * 生成jwt
//	 * 使用Hs256算法, 私匙使用固定秘钥
//	 *
//	 * @param secretKey jwt秘钥
//	 * @param ttlMillis jwt过期时间(毫秒)
//	 * @param claims    设置的信息
//	 * @return
//	 */
//	public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
//		// 指定签名的时候使用的签名算法，也就是header那部分
//		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//
//		// 生成JWT的时间
//		long expMillis = System.currentTimeMillis() + ttlMillis;
//		Date exp = new Date(expMillis);
//
//		// 设置jwt的body
//		JwtBuilder builder = Jwts.builder()
//				// 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
//				.setClaims(claims)
//				// 设置签名使用的签名算法和签名使用的秘钥
//				.signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
//				// 设置过期时间
//				.setExpiration(exp);
//
//		return builder.compact();
//	}
	public static String generateJWT(User user) {
		log.info("{}", System.currentTimeMillis());
//        Key key = Keys.hmacShaKeyFor(keyBytes.getBytes());
		String jwt = Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.setHeaderParam("alg", "HS256")
				.setSubject("user")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.JWT_EXIPIRE))
				.claim("id", user.getId())
				.claim("username", user.getUsername())
				.signWith(key)
				.compact();
		return jwt;
	}


	public static String getToken(HttpServletRequest request) {
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

	public static Claims getClaims(String token) {
		Key key = Keys.hmacShaKeyFor(keyBytes.getBytes());
		Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claims;
	}

	public static Long getUserId(String token) {
		Claims claims = getClaims(token);
		Integer idInt = (Integer) claims.get("id");
		Long id = idInt.longValue();
		return id;
	}

	public static String getUsername(String token) {
		Claims claims = getClaims(token);
		String username = (String) claims.get("username");
		return username;
	}

	public static Date getExpireTime(String token) {
		Claims claims = getClaims(token);
		Date expiration = claims.getExpiration();
		return expiration;
	}

	public static boolean isTokenNotExpired(String token) {
		return new Date().before(getExpireTime(token));
	}

	public static boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = getUsername(token);
		return (username.equals(userDetails.getUsername())) && isTokenNotExpired(token);
	}

	public static Long getUserId(HttpServletRequest request) {

		try {
			String token = JwtUtil.getToken(request);
			Long id = JwtUtil.getUserId(token);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Token解密
	 *
	 * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
	 * @param token     加密后的token
	 * @return
	 */
//	public static Claims parseJWT(String secretKey, String token) {
//		// 得到DefaultJwtParser
//		Claims claims = Jwts.parser()
//				// 设置签名的秘钥
//				.setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
//				// 设置需要解析的jwt
//				.parseClaimsJws(token).getBody();
//		return claims;
//	}

}
