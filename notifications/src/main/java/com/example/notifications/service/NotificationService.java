package com.example.notifications.service;

import com.example.notifications.command.CreateNotificationCommand;
import com.example.notifications.command.NotificationCommand;
import com.example.notifications.command.SendNotificationCommand;
import com.example.notifications.command.ToggleReadCommand;
import com.example.notifications.messages.NotificationMessage;
import com.example.notifications.model.Notification;
import com.example.notifications.constants.NotificationType;
import com.example.notifications.rabbitmq.RabbitMQConfig;
import com.example.notifications.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import com.example.notifications.observer.NotificationPublisher;
import com.example.notifications.observer.NotificationSubscriber;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPublisher publisher;

    @Autowired
    private NotificationSubscriber subscriber;

    private final NotificationCommandInvoker invoker;

    public NotificationService(NotificationRepository repo, NotificationCommandInvoker invoker) {
        this.notificationRepository = repo;
        this.invoker = invoker;
    }

    @PostConstruct
    public void init() {
        publisher.subscribe(subscriber);
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Optional<Notification> getNotificationById(Long id) {
        Optional<Notification> optional = notificationRepository.findById(id);
        optional.ifPresent(notification -> {
            if (!notification.isMarkAsRead()) {
                notification.setMarkAsRead(true);
                notificationRepository.save(notification);
            }
        });
        return optional;
    }

    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id).orElseThrow();
        NotificationCommand command = new ToggleReadCommand(n, true);
        invoker.setCommand(command);
        invoker.executeCommand();
        notificationRepository.save(n);
    }

    public void markAsUnread(Long id) {
        Notification n = notificationRepository.findById(id).orElseThrow();
        NotificationCommand command = new ToggleReadCommand(n, false);
        invoker.setCommand(command);
        invoker.executeCommand();
        notificationRepository.save(n);
    }

    public void send(List<Long> userIds) {
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void sendBatch(NotificationMessage message) {
        System.out.println("Received a " + NotificationMessage.class.getSimpleName() + " of type" + message.getType());

        sendBatch(message.getIds(), message.getType());
    }

    public void sendBatch(List<Long> ids, NotificationType type) {
        for (Long userId : ids) {
            NotificationCommand created = new CreateNotificationCommand(this, type, userId);
            invoker.addCommand(created);
        }
        NotificationCommand command = new SendNotificationCommand(this, ids);
        invoker.executeAll();
    }

    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndMarkAsReadFalse(userId);
    }

    public List<Notification> getUnreadByType(Long userId, NotificationType type) {
        return notificationRepository.findUnreadByType(userId, type);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void deleteAllByUserId(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }

    public void triggerObserverNotification(String message, Long userId, Long movieId, NotificationType type) {
        publisher.notifyObservers(message, userId, movieId, type);
    }

    public List<Notification> filterNotificationsBy(String methodName, Object expectedValue) {
        List<Notification> allNotifications = notificationRepository.findAll();
        List<Notification> filtered = new ArrayList<>();

        for (Notification notification : allNotifications) {
            try {
                Method method = notification.getClass().getMethod(methodName);
                Object result = method.invoke(notification);
                if (Objects.equals(result, expectedValue)) {
                    filtered.add(notification);
                }
            } catch (Exception e) {
                System.err.println("Reflection error: " + e.getMessage());
            }
        }

        return filtered;
    }
}
