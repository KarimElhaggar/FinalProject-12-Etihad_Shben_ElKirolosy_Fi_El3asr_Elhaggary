package com.example.reviews.rabbitmq;

import com.example.notifications.messages.NotificationMessage;
import com.example.notifications.rabbitmq.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendToNotifications(NotificationMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                message
        );
        System.out.println("Sent a " + NotificationMessage.class.getSimpleName() + " of type" + message.getType());
    }
}