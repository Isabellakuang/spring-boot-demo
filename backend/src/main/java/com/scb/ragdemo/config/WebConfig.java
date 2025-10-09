package com.scb.ragdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.lang.NonNull;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Web Configuration
 * Configures CORS policies and Web interceptors
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS cross-origin resource sharing
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")  // 允许所有源（支持 Kubernetes 环境）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        
        log.info("CORS configuration applied for /api/** endpoints with allowedOriginPatterns(*)");
    }

    /**
     * Configure interceptors
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // Add request logging interceptor
        registry.addInterceptor(new RequestLoggingInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health/**");
        
        log.info("Request logging interceptor registered");
    }

    /**
     * Request Logging Interceptor
     * Logs basic information for all API requests
     */
    @Slf4j
    static class RequestLoggingInterceptor implements HandlerInterceptor {
        
        @Override
        public boolean preHandle(
                @NonNull HttpServletRequest request,
                @NonNull HttpServletResponse response,
                @NonNull Object handler) {
            
            long startTime = System.currentTimeMillis();
            request.setAttribute("startTime", startTime);
            
            log.info("Incoming request: {} {} from {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());
            
            return true;
        }

        @Override
        public void afterCompletion(
                @NonNull HttpServletRequest request,
                @NonNull HttpServletResponse response,
                @NonNull Object handler,
                Exception ex) {
            
            Long startTime = (Long) request.getAttribute("startTime");
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                log.info("Request completed: {} {} - Status: {} - Duration: {}ms",
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        duration);
            }
            
            if (ex != null) {
                log.error("Request failed with exception", ex);
            }
        }
    }
}
