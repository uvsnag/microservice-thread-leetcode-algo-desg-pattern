package com.example.cacheservice.config;

import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- File Activity (Direct Exchange with Dead Letter Queue) ---

    @Bean
    Queue fileActivityQueue(@Value("${app.rabbitmq.file-activity-queue}") String queueName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", "file.activity.dlx")
                .withArgument("x-dead-letter-routing-key", "file.activity.dlq")
                .build();
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

    // --- Dead Letter Queue for file activity (DLQ pattern) ---

    @Bean
    DirectExchange fileActivityDlx() {
        return new DirectExchange("file.activity.dlx", true, false);
    }

    @Bean
    Queue fileActivityDlq() {
        return QueueBuilder.durable("file.activity.dlq").build();
    }

    @Bean
    Binding fileActivityDlqBinding(Queue fileActivityDlq, DirectExchange fileActivityDlx) {
        return BindingBuilder.bind(fileActivityDlq).to(fileActivityDlx).with("file.activity.dlq");
    }

    // --- Fanout Exchange for system-wide broadcast events ---

    @Bean
    FanoutExchange systemBroadcastExchange() {
        return new FanoutExchange("system.broadcast.exchange", true, false);
    }

    @Bean
    Queue cacheBroadcastQueue() {
        return QueueBuilder.durable("system.broadcast.cache.queue").build();
    }

    @Bean
    Binding cacheBroadcastBinding(Queue cacheBroadcastQueue, FanoutExchange systemBroadcastExchange) {
        return BindingBuilder.bind(cacheBroadcastQueue).to(systemBroadcastExchange);
    }
}
