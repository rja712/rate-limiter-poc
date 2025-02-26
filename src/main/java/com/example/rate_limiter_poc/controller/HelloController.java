package com.example.rate_limiter_poc.controller;

import com.example.rate_limiter_poc.annotation.RateLimit;
import com.example.rate_limiter_poc.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @RateLimit(requestLimit = 5)
    @GetMapping("{orgId}/hello")
    public String hello(@PathVariable("orgId") String orgId) {
        return helloService.sayHello();
    }

    @RateLimit(requestLimit = 3)
    @GetMapping("{orgId}/hi")
    public String hi(@PathVariable("orgId") String orgId) {
        return helloService.sayHi();
    }

    @GetMapping("{orgId}/bye")
    public String bye(@PathVariable("orgId") String orgId) {
        return helloService.sayBye();
    }


    @RateLimit(requestLimit = 10)
    @GetMapping("{orgId}/fuckOff")
    public String fuckOff(@PathVariable("orgId") String orgId) {
        return helloService.sayFuckOff();
    }
}