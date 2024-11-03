package com.example.oj.interceptor;

import com.example.oj.utils.BaseContext;
import com.example.oj.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.patterns.IToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
//@CrossOrigin
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
//        //判断当前拦截到的是Controller的方法还是其他资源
//        if (!(handler instanceof HandlerMethod)) {
////            //当前拦截到的不是动态方法，直接放行
//            return true;
//        }
//
//        Long id = JwtUtil.getUserId(request);
//        log.info("jwt interceptor, id:{}", id);

//        if (id == null){
////            response.sendRedirect(request.getContextPath()+"/login");
//            response.setStatus(401);
//            return false;
//        }
//        else{
//            BaseContext.setCurrentId(id);
//        }
//
//        return true;
  }


//

}
