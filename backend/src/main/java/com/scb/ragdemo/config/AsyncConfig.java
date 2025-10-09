package com.scb.ragdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Asynchronous Task Configuration
 * Configures thread pool for asynchronous task execution
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Configure thread pool task executor for asynchronous operations
     * Used for handling time-consuming tasks like document processing
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core thread pool size
        executor.setCorePoolSize(5);
        
        // Maximum thread pool size
        executor.setMaxPoolSize(10);
        
        // Queue capacity
        executor.setQueueCapacity(100);
        
        // Thread name prefix
        executor.setThreadNamePrefix("async-task-");
        
        // Rejection policy: caller runs the task when pool is full
        executor.setRejectedExecutionHandler(
                new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Maximum wait time on shutdown (seconds)
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }
}
