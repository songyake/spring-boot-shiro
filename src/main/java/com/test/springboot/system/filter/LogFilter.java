package com.test.springboot.system.filter;

import com.alibaba.fastjson.JSON;
import com.test.springboot.utils.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

/**
 * 日志过滤器
 */
@Slf4j
@Aspect
@Component
public class LogFilter {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final ThreadLocal<Long> startTimeMillis = new ThreadLocal<>();

    private static final ThreadLocal<Long> endTimeMillis = new ThreadLocal<>();

    @Pointcut("execution(* com.test.springboot.controller.*.*(..))")
    public void controller() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {
    }

    @Before("controller()")
    public void doBefore(JoinPoint joinPoint) {
        startTimeMillis.set(System.currentTimeMillis());
    }

    @After("controller()")
    public void doAfter(JoinPoint joinPoint) {
        endTimeMillis.set(System.currentTimeMillis());
    }

    @AfterReturning(pointcut = "controller()", returning = "returnValue")
    public JoinPoint afterReturning(JoinPoint joinPoint, Object returnValue) {
        org.springframework.web.context.request.RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        log.info("请求URI:{} 请求类型:{} 请求参数:{}",request.getRequestURI(),request.getMethod(),JSON.toJSONString(joinPoint.getArgs()));
        log.info("请求返回:{} 请求耗时: {}", JSON.toJSONString(returnValue)
                ,DateUtils.formatDateTime(endTimeMillis.get() - startTimeMillis.get()));
        return joinPoint;
    }

    @AfterThrowing(pointcut = "controller()", throwing = "e")
    public void afterThrowing(Throwable e) {
        log.error("ERROR:", new Object[] { e });
    }
}
