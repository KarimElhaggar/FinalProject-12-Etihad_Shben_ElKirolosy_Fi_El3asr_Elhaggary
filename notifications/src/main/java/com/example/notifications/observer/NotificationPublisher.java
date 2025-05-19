package com.example.notifications.observer;

import com.example.notifications.constants.NotificationType;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationPublisher {

    private final List<NotificationObserver> observers = new ArrayList<>();

    public void subscribe(NotificationObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(List<Long> userIds, NotificationType type) {
        for (NotificationObserver observer : observers) {
            observer.onNotificationReceived(userIds, type);
        }
    }
}
