package com.example.cacheservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.kafka.listener.auto-startup=false",
    "spring.rabbitmq.listener.simple.auto-startup=false",
    "spring.rabbitmq.listener.direct.auto-startup=false"
})
class CacheServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
