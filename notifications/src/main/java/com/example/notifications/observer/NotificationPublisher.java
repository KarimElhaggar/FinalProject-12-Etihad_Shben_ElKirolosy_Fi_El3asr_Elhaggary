//package com.example.notifications.observer;
//
//import com.example.notifications.constants.NotificationType;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class NotificationPublisher {
//
//    private final List<NotificationObserver> observers = new ArrayList<>();
//
//    public void subscribe(NotificationObserver observer) {
//        observers.add(observer);
//    }
//
//    public void notifyObservers(String message, Long userId, Long movieId, NotificationType type) {
//        for (NotificationObserver observer : observers) {
//            observer.update(message, userId, movieId, type);
//        }
//    }
//}
