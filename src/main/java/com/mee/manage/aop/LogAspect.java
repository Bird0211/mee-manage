package com.mee.manage.aop;

import com.mee.manage.controller.ManageController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(public * com.mee.manage.controller.*.*(..))")
    public void log(){}

    //统计请求的处理时间
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Before("log()")
    public void doBefore(JoinPoint joinPoint) throws Throwable{
        startTime.set(System.currentTimeMillis());
        //接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //记录请求的内容
        logger.info("URL: = {}",request.getRequestURL().toString());
        logger.info("Method: = {}",request.getMethod());
        logger.info("IP: = {}",request.getRemoteAddr());
        logger.info("Class_Method = {}",joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());

    }

    @AfterReturning(returning = "ret" , pointcut = "log()")
    public void doAfterReturning(Object ret){
        //处理完请求后，返回内容
        logger.info("方法返回值: = {}",ret);
        logger.info("方法执行时间: = {} ms", (System.currentTimeMillis() - startTime.get()));
    }

}
