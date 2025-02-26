package com.example.rate_limiter_poc.filter;

import com.example.rate_limiter_poc.annotation.RateLimit;
import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter implements WebFilter {

    private static final String RATE_LIMIT_KEY_PREFIX = "RateLimit";
    private static final Pattern ORG_ID_PATTERN = Pattern.compile("/api\\/(\\d+)\\/.*");

    private final ProxyManager<byte[]> proxyManager;
    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return handlerMapping.getHandler(exchange)
                .cast(HandlerMethod.class)
                .flatMap(handlerMethod -> {
                    RateLimit rateLimit = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RateLimit.class);
                    if (rateLimit != null) {
                        return applyRateLimiting(exchange, chain, handlerMethod, rateLimit);
                    }
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

    private Mono<Void> applyRateLimiting(ServerWebExchange exchange, WebFilterChain chain, HandlerMethod handlerMethod, RateLimit rateLimit) {
        String key = generateKey(exchange, handlerMethod);
        Bucket bucket = proxyManager.builder().build(key.getBytes(StandardCharsets.UTF_8), () -> createBucketConfig(rateLimit));

        if (bucket.tryConsume(1)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return exchange.getResponse().setComplete();
    }


    private BucketConfiguration createBucketConfig(RateLimit rateLimit) {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(rateLimit.requestLimit())
                        .refillIntervally(rateLimit.requestLimit(), Duration.ofSeconds(rateLimit.durationInSeconds()))
                        .build())
                .build();
    }

    private String generateKey(ServerWebExchange exchange, HandlerMethod handlerMethod) {
        String tenantId = Optional.ofNullable(exchange)
                .map(ServerWebExchange::getRequest)
                .map(ServerHttpRequest::getURI)
                .map(URI::getPath)
                .map(path -> {
                    Matcher matcher = ORG_ID_PATTERN.matcher(path);
                    return matcher.find() ? matcher.group(1) : "";
                })
                .orElse("");


        String ip = Optional.ofNullable(exchange)
                .map(ServerWebExchange::getRequest)
                .map(ServerHttpRequest::getRemoteAddress)
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .map(address -> address.replace(":", "."))
                .orElse("unknown");
        return String.format("%s-%s-%s-%s-%s",
                RATE_LIMIT_KEY_PREFIX,
                tenantId,
                handlerMethod.getBeanType().getSimpleName(),
                handlerMethod.getMethod().getName(),
                ip);
    }
}