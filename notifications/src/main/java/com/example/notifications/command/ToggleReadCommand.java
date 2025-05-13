package com.example.notifications.command;

import com.example.notifications.model.Notification;

public class ToggleReadCommand implements NotificationCommand {
    private final Notification notification;
    private final boolean newState;
    private boolean oldState;

    public ToggleReadCommand(Notification notification, boolean newState) {
        this.notification = notification;
        this.newState = newState;
    }

    @Override
    public void execute() {
        oldState = notification.isMarkAsRead();
        notification.setMarkAsRead(newState);
    }

    @Override
    public void undo() {
        notification.setMarkAsRead(oldState);
    }
}
