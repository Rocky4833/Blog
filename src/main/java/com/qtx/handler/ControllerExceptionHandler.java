package com.qtx.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
//异常处理器
@ControllerAdvice
public class ControllerExceptionHandler {
    //获取日志对象
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request,Exception e) throws Exception {
        //日志对象记录信息
        logger.error("Request URL : {},Exception : {}",request.getRequestURI(),e);

        //让springboot处理一些标有状态码注解的异常类
        if(AnnotationUtils.findAnnotation(e.getClass(),ResponseStatus.class) != null){
            throw e;
        }
        //创建视图对象
        ModelAndView mv = new ModelAndView();
        //添加展示信息到视图对象中
        mv.addObject("url",request.getRequestURI());
        mv.addObject("exception", e);
        //视图对象通过指定页面展示
        mv.setViewName("error/error");
        return mv;
    }
}
