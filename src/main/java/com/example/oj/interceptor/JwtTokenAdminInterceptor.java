package com.example.oj.interceptor;

import com.example.oj.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT token validation interceptor
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
		//        // Determine if the current interception is a Controller method or other resources
		//        if (!(handler instanceof HandlerMethod)) {
		//            // If it's not a dynamic method, allow it to pass
		//            return true;
		//        }
		//
		//        Long id = JwtUtil.getUserId(request);
		//        log.info("JWT interceptor, id:{}", id);
		//
		//        if (id == null){
		//            //response.sendRedirect(request.getContextPath()+"/login");
		//            response.setStatus(401);
		//            return false;
		//        }
		//        else{
		//            BaseContext.setCurrentId(id);
		//        }
		//
		//        return true;
	}

}
