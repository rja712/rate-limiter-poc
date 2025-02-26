package com.example.rate_limiter_poc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int requestLimit() default Integer.MAX_VALUE;
    long durationInSeconds() default 60;
    boolean isIpSpecific() default false;
}