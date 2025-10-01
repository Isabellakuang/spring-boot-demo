package com.java.demo.health;

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
                                RedisConnectionFactory redisConnectionFactory) {
        this.dataSource = dataSource;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public Health health() {
        try {
            // Check database connection
            boolean dbHealthy = checkDatabase();
            
            // Check Redis connection
            boolean redisHealthy = checkRedis();
            
            if (dbHealthy && redisHealthy) {
                return Health.up()
                        .withDetail("database", "UP")
                        .withDetail("redis", "UP")
                        .withDetail("message", "All systems operational")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", dbHealthy ? "UP" : "DOWN")
                        .withDetail("redis", redisHealthy ? "UP" : "DOWN")
                        .withDetail("message", "Some systems are down")
                        .build();
            }
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
        try {
            redisConnectionFactory.getConnection().ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
