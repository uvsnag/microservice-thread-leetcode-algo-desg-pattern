package com.example.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- File Activity (Direct Exchange) ---

    @Bean
    Queue notificationFileActivityQueue(@Value("${app.rabbitmq.file-activity-queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    DirectExchange fileActivityExchange(@Value("${app.rabbitmq.file-activity-exchange}") String exchangeName) {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    Binding notificationFileActivityBinding(
            Queue notificationFileActivityQueue,
            DirectExchange fileActivityExchange,
            @Value("${app.rabbitmq.file-activity-routing-key}") String routingKey
    ) {
        return BindingBuilder.bind(notificationFileActivityQueue).to(fileActivityExchange).with(routingKey);
    }

    // --- Fanout Exchange for system-wide broadcast events ---

    @Bean
    FanoutExchange systemBroadcastExchange() {
        return new FanoutExchange("system.broadcast.exchange", true, false);
    }

    @Bean
    Queue notificationBroadcastQueue() {
        return QueueBuilder.durable("system.broadcast.notification.queue").build();
    }

    @Bean
    Binding notificationBroadcastBinding(Queue notificationBroadcastQueue, FanoutExchange systemBroadcastExchange) {
        return BindingBuilder.bind(notificationBroadcastQueue).to(systemBroadcastExchange);
    }

    // --- Topic Exchange for wildcard routing (event.user.*, event.file.*) ---

    @Bean
    TopicExchange eventTopicExchange() {
        return new TopicExchange("event.topic.exchange", true, false);
    }

    @Bean
    Queue allEventsQueue() {
        return QueueBuilder.durable("event.topic.all.queue").build();
    }

    @Bean
    Binding allEventsBinding(Queue allEventsQueue, TopicExchange eventTopicExchange) {
        return BindingBuilder.bind(allEventsQueue).to(eventTopicExchange).with("event.#");
    }
}
