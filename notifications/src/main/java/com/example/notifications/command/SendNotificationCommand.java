package com.example.notifications.command;

import com.example.notifications.constants.NotificationType;
import com.example.notifications.service.EmailService;
import com.example.notifications.service.NotificationService;

import java.util.List;

public class SendNotificationCommand implements NotificationCommand {
    private final List<String> userEmails;
    private final NotificationType type;
    private final EmailService emailService;

    public SendNotificationCommand(List<String> userEmails, NotificationType type, EmailService emailService) {
        this.userEmails = userEmails;
        this.type = type;
        this.emailService = emailService;
    }

    @Override
    public void execute() {
        for (String userEmail : userEmails) {
            emailService.sendNotificationEmail(userEmail, type);
        }
    }

    @Override
    public void undo() {}
}
