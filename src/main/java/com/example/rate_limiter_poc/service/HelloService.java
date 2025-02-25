package com.example.rate_limiter_poc.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String sayHello() {
        return "Hello, World!";
    }

    public String sayHi() {
        return "Hi, How are you";
    }

    public String sayBye() {
        return "Goodbye!";
    }

    public String sayFuckOff() {
        return "Fuck Off!";
    }
}