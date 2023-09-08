package com.example.redis.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class DemoController {

    private StringRedisTemplate template;

    public DemoController(StringRedisTemplate template) {
        this.template = template;
    }

    @GetMapping("/put")
    public String redisSet() {
        int i = (int)(Math.random() * 100);
        template.opsForValue().set("key"+i, "value"+i, 300, TimeUnit.SECONDS);
        return "success "+"key"+i;
    }

    @GetMapping("/set")
    public String set() {
        int i = (int)(Math.random() * 100);
        template.opsForValue().set("lcc", "value"+i, 30000, TimeUnit.SECONDS);
        return "success "+"key"+i;
    }

}
