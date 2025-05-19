package com.example.notifications.model;

import com.example.notifications.constants.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;
    private String notification;
    private NotificationType notificationType;
    private boolean markAsRead;
    private LocalDateTime notificationDate;

    private Long userId;
    private Long movieId;

    public Notification(String notification, NotificationType notificationType) {
        this.notification = notification;
        this.notificationType = notificationType;
        this.markAsRead = false;
        this.notificationDate = LocalDateTime.now();
    }

    public Notification(String notification, NotificationType notificationType, Long userId) {
        this.notification = notification;
        this.notificationType = notificationType;
        this.markAsRead = false;
        this.notificationDate = LocalDateTime.now();
        this.userId = userId;
    }

    public Notification(String notification, NotificationType notificationType, Long userId, Long movieId) {
        this.notification = notification;
        this.notificationType = notificationType;
        this.markAsRead = false;
        this.notificationDate = LocalDateTime.now();
        this.userId = userId;
        this.movieId = movieId;
    }

    public Notification(
            String notification,
            NotificationType notificationType,
            boolean markAsRead,
            Long userId, Long movieId) {
        this.notification = notification;
        this.notificationType = notificationType;
        this.markAsRead = markAsRead;
        this.notificationDate = LocalDateTime.now();
        this.userId = userId;
        this.movieId = movieId;
    }
}