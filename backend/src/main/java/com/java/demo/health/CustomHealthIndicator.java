package com.java.demo.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Custom health indicator for enterprise monitoring.
 * Checks database and Redis connectivity.
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    private final RedisConnectionFactory redisConnectionFactory;

    public CustomHealthIndicator(DataSource dataSource, 
                                @Autowired(required = false) RedisConnectionFactory redisConnectionFactory) {
        this.dataSource = dataSource;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public Health health() {
        try {
            // Check database connection
            boolean dbHealthy = checkDatabase();
            
            // Check Redis connection (only if available)
            boolean redisHealthy = redisConnectionFactory != null ? checkRedis() : true;
            
            Health.Builder builder = dbHealthy && redisHealthy ? Health.up() : Health.down();
            builder.withDetail("database", dbHealthy ? "UP" : "DOWN");
            
            if (redisConnectionFactory != null) {
                builder.withDetail("redis", redisHealthy ? "UP" : "DOWN");
            } else {
                builder.withDetail("redis", "NOT_CONFIGURED");
            }
            
            builder.withDetail("message", 
                dbHealthy && redisHealthy ? "All systems operational" : "Some systems are down");
            
            return builder.build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private boolean checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkRedis() {
        if (redisConnectionFactory == null) {
            return true;
        }
        try {
            redisConnectionFactory.getConnection().ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
