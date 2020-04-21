package com.mee.manage.aop;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 打印请求相关参数
        logger.info("========================================== Start ==========================================");
        // 打印请求 url
        logger.info("URL            : {}", request.getRequestURL().toString());
        // 打印 Http method
        logger.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        logger.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        logger.info("IP             : {}", request.getRemoteAddr());
        // 打印请求入参
        try {
            logger.info("Request Args   : {}", JSON.toJSON(joinPoint.getArgs()));
        }catch (Exception e) {
            logger.info("Request Args Error",e);
        }


    }

    /**
     * 环绕
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("log()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // 打印出参
        logger.info("Response Args  : {}", JSON.toJSON(result));
        // 执行耗时
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 在切点之后织入
     * @throws Throwable
     */
    @After("log()")
    public void doAfter() throws Throwable {
        logger.info("=========================================== End ===========================================");
        // 每个请求之间空一行
        logger.info("");
    }

}
