package com.example.cacheservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI cacheServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cache Service API")
                        .description("Redis-backed caching service. Consumes Kafka user events and RabbitMQ file events.")
                        .version("1.0.0"));
    }
}
