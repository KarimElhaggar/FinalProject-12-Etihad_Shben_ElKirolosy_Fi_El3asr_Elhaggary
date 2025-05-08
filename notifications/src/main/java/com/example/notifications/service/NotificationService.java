package com.example.notifications.service;

import com.example.notifications.command.NotificationCommand;
import com.example.notifications.command.SendNotificationCommand;
import com.example.notifications.command.ToggleReadCommand;
import com.example.notifications.model.Notification;
import com.example.notifications.constants.NotificationType;
import com.example.notifications.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import com.example.notifications.observer.NotificationPublisher;
import com.example.notifications.observer.NotificationSubscriber;
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

    public void send(Notification notification) {
        System.out.println("Sending: " + notification.getNotification());
    }

    public void sendBatch(List<Long> ids) {
        for (Long id : ids) {
            Notification n = notificationRepository.findById(id).orElseThrow();
            NotificationCommand command = new SendNotificationCommand(n, this);
            invoker.addCommand(command);
        }

        invoker.executeAll();
        List<Notification> updated = notificationRepository.findAllById(ids);
        notificationRepository.saveAll(updated);
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

}
