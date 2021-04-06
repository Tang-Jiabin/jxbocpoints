package com.zykj.pointsmall.common.interceptor;

import com.zykj.pointsmall.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 自定义拦截器，判断此次请求是否有权限
 *
 * @author J.Tang
 */
@Slf4j
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final TokenManager manager;

    @Autowired
    public AuthorizationInterceptor(TokenManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();


        Authorization annotation = method.getAnnotation(Authorization.class);
        if (annotation == null) {
            return true;
        }
        String token = request.getHeader("X-Auth-Token");

        if (!StringUtils.hasText(token)) {
            response.getOutputStream().write(ServerResponse.createMessage(401, "请重新登录").toJson().getBytes("UTF-8"));
            return false;
        }
        try {

            if (manager.checkToken(token)) {
                request.setAttribute("token", manager.getToken(token).getUserId());
                return true;
            }
        } catch (Exception e) {
            response.getOutputStream().write(ServerResponse.createMessage(401, "请重新登录").toJson().getBytes("UTF-8"));
            return false;
        }
        response.getOutputStream().write(ServerResponse.createMessage(401, "请重新登录").toJson().getBytes("UTF-8"));
        return false;
    }


}
