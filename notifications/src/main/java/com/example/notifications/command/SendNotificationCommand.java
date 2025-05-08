package com.example.notifications.command;

import com.example.notifications.model.Notification;
import com.example.notifications.service.NotificationService;

public class SendNotificationCommand implements NotificationCommand {

    private final NotificationService notificationService;
    private final Notification notification;

    public SendNotificationCommand(NotificationService notificationService, Notification notification) {
        this.notificationService = notificationService;
        this.notification = notification;
    }

    @Override
    public void execute() {
        notificationService.saveNotification(notification);
    }

    @Override
    public void undo() {}
}
