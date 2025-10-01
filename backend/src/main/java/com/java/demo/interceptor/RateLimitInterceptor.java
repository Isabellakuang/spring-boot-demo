package com.java.demo.interceptor;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> rateLimitBuckets;
    private final com.java.demo.config.RateLimitConfig rateLimitConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        String key = getClientKey(request);
        
        Bucket bucket = rateLimitBuckets.computeIfAbsent(key, 
            k -> rateLimitConfig.createBucket(k, 100, Duration.ofMinutes(1)));

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
            return false;
        }
    }

    private String getClientKey(HttpServletRequest request) {
        String username = request.getUserPrincipal() != null ? 
            request.getUserPrincipal().getName() : "anonymous";
        return username + "_" + request.getRemoteAddr();
    }
}