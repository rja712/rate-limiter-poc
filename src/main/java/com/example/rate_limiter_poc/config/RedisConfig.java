package com.example.rate_limiter_poc.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Bean
    public RedisClient redisClient() {
        return RedisClient.
                create(RedisURI.builder().
                        withHost("localhost").
                        withPort(6379).
                        build());
    }

    @Bean
    public LettuceBasedProxyManager lettuceBasedProxyManager(RedisClient redisClient) {
        return LettuceBasedProxyManager
                .builderFor(redisClient)
                .build();

    }
}