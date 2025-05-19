package com.example.notifications.rabbitmq;

import com.example.notifications.constants.NotificationType;
import com.example.notifications.rabbitmq.RabbitMQConfig;
import com.example.notifications.observer.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationPublisher notificationPublisher;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void receiveMessage(String message) {
        log.info("Received message: {}", message);

        String[] parts = message.split(";");
        List<Long> userIds = Arrays.stream(parts[0].split(","))
                .map(Long::parseLong)
                .toList();

        NotificationType type = NotificationType.valueOf(parts[1]);

        log.info("Parsed type: {}, userIds: {}", type, userIds);

        // Notify all observers (currently just NotificationService)
        notificationPublisher.notifyObservers(userIds, type);
    }
}
