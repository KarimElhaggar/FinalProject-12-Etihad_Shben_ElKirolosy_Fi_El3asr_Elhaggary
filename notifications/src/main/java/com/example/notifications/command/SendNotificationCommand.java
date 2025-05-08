package com.example.notifications.command;

import com.example.notifications.model.Notification;
import com.example.notifications.service.NotificationService;

public class SendNotificationCommand implements NotificationCommand {
    private final NotificationService notificationService;
    private final Notification notification;

    public SendNotificationCommand(Notification notification, NotificationService notificationService) {
        this.notification = notification;
        this.notificationService = notificationService;
    }

    @Override
    public void execute() {
        notificationService.send(notification);
    }

    @Override
    public void undo() {}
}
