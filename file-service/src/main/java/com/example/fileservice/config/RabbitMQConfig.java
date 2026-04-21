package com.example.fileservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    Queue fileActivityQueue(@Value("${app.rabbitmq.file-activity-queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    DirectExchange fileActivityExchange(@Value("${app.rabbitmq.file-activity-exchange}") String exchangeName) {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    Binding fileActivityBinding(
            Queue fileActivityQueue,
            DirectExchange fileActivityExchange,
            @Value("${app.rabbitmq.file-activity-routing-key}") String routingKey
    ) {
        return BindingBuilder.bind(fileActivityQueue).to(fileActivityExchange).with(routingKey);
    }
}
