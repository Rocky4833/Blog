package com.qtx.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    /**
     * @Pointcut:声明log方法是一个切面
     * execution():指定切面拦截哪些类
     */
    @Pointcut("execution(* com.qtx.web.*.*(..))")
    public void log(){
    }

    //该方法体内容在log()切面之前执行
    //日志处理：在切面log()执行之前记录url,ip,classMethod,args到日志中
    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        //1.获取HttpServletRequest对象
        //1.1 获取ServletRequestAttributes对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //1.2
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();//通过request对象获取请求路径
        String ip = request.getRemoteAddr();//通过request对象获取ip地址
        String classMethod = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();//通过切面中的对象joinpoint获取请求的某个类某个方法
        Object[] args = joinPoint.getArgs();
        RequestLog requestLog = new RequestLog(url, ip, classMethod, args);
        logger.info("Request : {}",requestLog);
    }

    //该方法体内容在切面log()之后执行
    @After("log()")
    public void doAfter(){
//        logger.info("------doAfter--------");
    }

   //在切面log()之后拦截返回值
   // 日志处理
    @AfterReturning(returning = "result",pointcut = "log()")
    public void doAfterReturn(Object result){
        logger.info("Result:{}",result);

    }

    //定义类接收请求信息 封装成一个RequestLog对象
    private class RequestLog{
        private String url;
        private String ip;
        private String classMethod;
        private Object[] args;

        public RequestLog(String url, String ip, String classMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
        }

        @Override
        public String toString() {
            return "{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }
}
