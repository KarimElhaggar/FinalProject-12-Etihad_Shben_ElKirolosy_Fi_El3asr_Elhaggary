package com.example.notifications.model;

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
    private Long id;
    private String notification;
    private NotificationType notificationType;
    private boolean markAsRead;
    private LocalDateTime notificationDate;

    private Long userId;
    private Long movieId;

    public Notification(String notification, NotificationType notificationType, boolean markAsRead, LocalDateTime notificationDate) {
        this.notification = notification;
        this.notificationType = notificationType;
        this.markAsRead = markAsRead;
        this.notificationDate = notificationDate;
    }

    public Notification(String notification, NotificationType notificationType, boolean markAsRead, LocalDateTime notificationDate, Long userId) {
        this.notification = notification;
        this.notificationType = notificationType;
        this.markAsRead = markAsRead;
        this.notificationDate = notificationDate;
        this.userId = userId;
    }

    public Notification(String notification, NotificationType notificationType, boolean markAsRead, LocalDateTime notificationDate, Long userId, Long movieId) {
        this.notification = notification;
        this.notificationType = notificationType;
        this.markAsRead = markAsRead;
        this.notificationDate = notificationDate;
        this.userId = userId;
        this.movieId = movieId;
    }
}