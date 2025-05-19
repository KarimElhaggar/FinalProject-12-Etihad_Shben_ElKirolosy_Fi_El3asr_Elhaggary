package com.example.notifications.command;

import com.example.notifications.constants.NotificationType;
import com.example.notifications.model.Notification;
import com.example.notifications.service.NotificationService;

import java.time.LocalDateTime;

public class CreateNotificationCommand implements NotificationCommand {
    private final NotificationService notificationService;
    private Long userId;
    private NotificationType notificationType;

    public CreateNotificationCommand(NotificationService notificationService, NotificationType notificationType, Long userId) {
        this.notificationService = notificationService;
        this.notificationType = notificationType;
        this.userId = userId;
    }

    @Override
    public void execute() {
        Notification notification = new Notification("Message", notificationType, userId);
        notificationService.saveNotification(notification);
    }

    @Override
    public void undo() {}
}
