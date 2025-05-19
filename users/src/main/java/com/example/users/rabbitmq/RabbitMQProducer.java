package com.example.users.rabbitmq;


import com.example.contracts.ReviewRequest;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange exchange;

    @Autowired
    public RabbitMQProducer(RabbitTemplate rabbitTemplate, TopicExchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void sendReviewRequest(ReviewRequest reviewRequest) {
        rabbitTemplate.convertAndSend(exchange.getName(), "users_routing_key", reviewRequest);
    }
}
