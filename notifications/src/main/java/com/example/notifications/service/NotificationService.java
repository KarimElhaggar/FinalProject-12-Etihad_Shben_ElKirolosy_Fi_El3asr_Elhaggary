package com.example.notifications.service;

import com.example.notifications.model.Notification;
import com.example.notifications.constants.NotificationType;
import com.example.notifications.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

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
        return notificationRepository.findById(id);
    }

    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndMarkAsReadFalse(userId);
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

    public List<Notification> getUnreadByType(Long userId, NotificationType type) {
        return notificationRepository.findUnreadByType(userId, type);
    }


    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void deleteAllByUserId(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }
}