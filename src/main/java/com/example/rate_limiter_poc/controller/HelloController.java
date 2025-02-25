package com.example.rate_limiter_poc.controller;

import com.example.rate_limiter_poc.annotation.RateLimit;
import com.example.rate_limiter_poc.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @RateLimit(limit = 5)
    @GetMapping("/hello")
    public String hello() {
        return helloService.sayHello();
    }

    @RateLimit(limit = 3)
    @GetMapping("/hi")
    public String hi() {
        return helloService.sayHi();
    }

    @GetMapping("/bye")
    public String bye() {
        return helloService.sayBye();
    }


    @RateLimit(limit = 10)
    @GetMapping("/fuckOff")
    public String fuckOff() {
        return helloService.sayFuckOff();
    }
}