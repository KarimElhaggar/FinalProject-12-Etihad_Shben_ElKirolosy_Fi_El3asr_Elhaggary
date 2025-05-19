package com.example.notifications.observer;

import com.example.notifications.constants.NotificationType;

import java.util.List;

public interface NotificationObserver {
    void onNotificationReceived(List<Long> userIds, NotificationType type);
}