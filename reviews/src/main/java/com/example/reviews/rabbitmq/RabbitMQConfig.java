package com.example.reviews.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String NOTIFICATION_QUEUE = "notification_queue";
    public static final String EXCHANGE = "shared_exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification_routing_key";

    public static final String USERS_QUEUE = "users_queue";
    public static final String USERS_ROUTING_KEY = "users_routing_key";

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE);
    }

    @Bean
    public Queue usersQueue() {
        return new Queue(USERS_QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(exchange)
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding usersBinding(Queue usersQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(usersQueue)
                .to(exchange)
                .with(USERS_ROUTING_KEY);
    }
}