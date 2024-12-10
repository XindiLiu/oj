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
public class LoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Wrap the request and response to enable multiple reads
		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

		long startTime = System.currentTimeMillis();
		filterChain.doFilter(wrappedRequest, wrappedResponse);
		long duration = System.currentTimeMillis() - startTime;

		// Log request details
		logRequest(wrappedRequest);

		// Log response details
		logResponse(wrappedResponse, duration);

		// Copy content of response back to original response
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

		// Log headers
//        Enumeration<String> headerNames = request.getHeaderNames();
//        msg.append("\nHeaders: ");
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            msg.append(headerName).append("=").append(request.getHeader(headerName)).append(", ");
//        }

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

		// Log headers
//        Collection<String> headerNames = response.getHeaderNames();
//        msg.append("\nHeaders: ");
//        for (String headerName : headerNames) {
//            msg.append(headerName).append("=").append(response.getHeader(headerName)).append(", ");
//        }

		// Log body
		byte[] content = response.getContentAsByteArray();
		if (content.length > 0) {
			String body = new String(content, response.getCharacterEncoding());
			msg.append("\nBody: ").append(body);
		}

		log.info(msg.toString());
	}
}