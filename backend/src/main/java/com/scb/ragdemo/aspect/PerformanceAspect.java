package com.scb.ragdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Performance Monitoring Aspect
 * Monitors method execution time and logs warnings for slow operations
 */
@Slf4j
@Aspect
@Component
public class PerformanceAspect {

    // Performance threshold definitions (in milliseconds)
    private static final long SLOW_THRESHOLD_MS = 1000;
    private static final long VERY_SLOW_THRESHOLD_MS = 3000;

    /**
     * Pointcut definition for all public methods in Controller and Service implementation classes
     */
    @Pointcut("execution(public * com.scb.ragdemo.controller..*.*(..)) || " +
              "execution(public * com.scb.ragdemo.service.impl..*.*(..))")
    public void performanceMonitoring() {
    }

    /**
     * Monitor method execution time and log performance warnings
     */
    @Around("performanceMonitoring()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "." + methodName + "()";
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Log warnings based on execution time thresholds
            if (executionTime >= VERY_SLOW_THRESHOLD_MS) {
                log.warn("⚠️ VERY SLOW: {} took {} ms (threshold: {} ms)",
                        fullMethodName, executionTime, VERY_SLOW_THRESHOLD_MS);
            } else if (executionTime >= SLOW_THRESHOLD_MS) {
                log.warn("⚠️ SLOW: {} took {} ms (threshold: {} ms)",
                        fullMethodName, executionTime, SLOW_THRESHOLD_MS);
            } else {
                log.debug("✓ Performance OK: {} took {} ms",
                        fullMethodName, executionTime);
            }
            
            return result;
            
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("✗ Performance monitoring failed for {} after {} ms",
                    fullMethodName, executionTime);
            throw throwable;
        }
    }

    /**
     * Pointcut definition for document upload and processing methods
     */
    @Pointcut("execution(* com.scb.ragdemo.service.impl.DocumentServiceImpl.uploadDocument(..)) || " +
              "execution(* com.scb.ragdemo.service.impl.DocumentServiceImpl.processDocument(..))")
    public void documentProcessing() {
    }

    /**
     * Monitor document processing performance
     */
    @Around("documentProcessing()")
    public Object monitorDocumentProcessing(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        log.info("📄 Starting document processing: {}", methodName);
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("✓ Document processing completed: {} in {} ms",
                    methodName, executionTime);
            
            // Warn if document processing takes longer than 10 seconds
            if (executionTime > 10000) {
                log.warn("⚠️ Document processing took longer than expected: {} ms",
                        executionTime);
            }
            
            return result;
            
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("✗ Document processing failed: {} after {} ms",
                    methodName, executionTime);
            throw throwable;
        }
    }

    /**
     * Pointcut definition for query processing methods in RouterService
     */
    @Pointcut("execution(* com.scb.ragdemo.service.impl.RouterServiceImpl.route(..)) || " +
              "execution(* com.scb.ragdemo.service.impl.RouterServiceImpl.queryWith*(..))")
    public void queryProcessing() {
    }

    /**
     * Monitor query processing performance
     */
    @Around("queryProcessing()")
    public Object monitorQueryProcessing(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        long startTime = System.currentTimeMillis();
        log.info("🔍 Starting query processing: {} with args: {}",
                methodName, args.length > 0 ? args[0] : "none");
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("✓ Query processing completed: {} in {} ms",
                    methodName, executionTime);
            
            // Warn if query takes longer than 5 seconds
            if (executionTime > 5000) {
                log.warn("⚠️ Query processing took longer than expected: {} ms",
                        executionTime);
            }
            
            return result;
            
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("✗ Query processing failed: {} after {} ms",
                    methodName, executionTime);
            throw throwable;
        }
    }
}
