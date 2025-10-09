package com.scb.ragdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Logging Aspect
 * Automatically logs method entry, exit, exceptions and execution time for all Service layer methods
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Pointcut definition for all public methods in Service implementation classes
     */
    @Pointcut("execution(public * com.scb.ragdemo.service.impl.*.*(..))")
    public void serviceLayer() {
    }

    /**
     * Log before method execution
     */
    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info(">>> Entering {}.{}() with arguments: {}",
                className, methodName, Arrays.toString(args));
    }

    /**
     * Log after successful method execution with return value
     */
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.info("<<< Exiting {}.{}() with result: {}",
                className, methodName, result);
    }

    /**
     * Log when method throws an exception
     */
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.error("!!! Exception in {}.{}(): {}",
                className, methodName, exception.getMessage(), exception);
    }

    /**
     * Log method execution time and handle both success and failure cases
     */
    @Around("serviceLayer()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("=== {}.{}() executed in {} ms",
                    className, methodName, executionTime);
            
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.error("=== {}.{}() failed after {} ms",
                    className, methodName, executionTime);
            
            throw throwable;
        }
    }
}
