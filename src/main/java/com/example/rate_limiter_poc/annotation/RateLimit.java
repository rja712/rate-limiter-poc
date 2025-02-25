package com.example.rate_limiter_poc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int limit() default Integer.MAX_VALUE;
    long durationSeconds() default 60; // Duration in seconds, defaults to 1 minute
}