package com.example.notifications.command;

import com.example.notifications.model.Notification;
import com.example.notifications.service.NotificationService;

import java.util.List;

public class SendNotificationCommand implements NotificationCommand {
    private final NotificationService notificationService;
    private List<Long> userIds;

    public SendNotificationCommand(NotificationService notificationService, List<Long> userIds) {
        this.notificationService = notificationService;
        this.userIds = userIds;
    }

    @Override
    public void execute() {
        notificationService.send(userIds);
    }

    @Override
    public void undo() {}
}
