package com.example.notifications.service;

import com.example.notifications.model.Notification;
import com.example.notifications.constants.NotificationType;
import com.example.notifications.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import com.example.notifications.observer.NotificationPublisher;
import com.example.notifications.observer.NotificationSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPublisher publisher;

    @Autowired
    private NotificationSubscriber subscriber;

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

    public Notification markAsRead(Long id) {
        Optional<Notification> optional = notificationRepository.findById(id);
        if (optional.isPresent()) {
            Notification notification = optional.get();
            notification.setMarkAsRead(true);
            return notificationRepository.save(notification);
        }
        return null;
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
