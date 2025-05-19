package com.example.reviews.rabbitmq;

import com.example.reviews.constants.NotificationType;
import com.example.reviews.observer.ReviewObserver;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RabbitMQProducer implements ReviewObserver {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void onReviewEvent(List<Long> ids, NotificationType notificationType) {
        StringBuilder message = new StringBuilder();

        for (int i = 0; i < ids.size() - 1; i++) {
           message.append(ids.get(i)).append(",");
        }

        if (!ids.isEmpty()) {
            message.append(ids.getLast());
        }

        message.append(";").append(notificationType);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                message.toString()
                );
        System.out.println("Sent a notification of type" + notificationType);
    }
}