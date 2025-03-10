package com.example.oj.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

@Slf4j
@Component
/*
 * Log http requests and responses.
 */
public class
LoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Wrap the request and response to enable multiple reads
		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

		logRequest(wrappedRequest);

		long startTime = System.currentTimeMillis();
		filterChain.doFilter(wrappedRequest, wrappedResponse);
		long duration = System.currentTimeMillis() - startTime;

		logResponse(wrappedResponse, duration);
		wrappedResponse.copyBodyToResponse();
	}

	private void logRequest(ContentCachingRequestWrapper request) throws IOException {
		StringBuilder msg = new StringBuilder();
		msg.append("Request: ")
				.append(request.getMethod())
				.append(" ")
				.append(request.getRequestURI());

		String queryString = request.getQueryString();
		if (queryString != null) {
			msg.append("?").append(queryString);
		}

		// Log body
		byte[] content = request.getContentAsByteArray();
		if (content.length > 0) {
			String body = new String(content, request.getCharacterEncoding());
			msg.append("\nBody: ").append(body);
		}

		log.info(msg.toString());
	}

	private void logResponse(ContentCachingResponseWrapper response, long duration) throws IOException {
		StringBuilder msg = new StringBuilder();
		msg.append("Response: ")
				.append("Status=").append(response.getStatus())
				.append(", Time Taken=").append(duration).append("ms");

		// Log body
		byte[] content = response.getContentAsByteArray();
		if (content.length > 0) {
			String body = new String(content, response.getCharacterEncoding());
			msg.append("\nBody: ").append(body);
		}

		log.info(msg.toString());
	}
}