package com.qtx.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//登入过滤器
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

      if(request.getSession().getAttribute("user") == null){
          response.sendRedirect("/admin");//重定向到登入页面
          return false;//程序结束执行
      }
        return true;//程序继续执行
    }
}
