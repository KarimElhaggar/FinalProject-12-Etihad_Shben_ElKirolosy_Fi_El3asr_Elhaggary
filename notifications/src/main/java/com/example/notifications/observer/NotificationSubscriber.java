//package com.example.notifications.observer;
//
//import com.example.notifications.constants.NotificationType;
//import com.example.notifications.model.Notification;
//import com.example.notifications.service.NotificationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//import java.time.LocalDateTime;
//
//@Component
//public class NotificationSubscriber implements NotificationObserver {
//
//    @Autowired
//    private NotificationService notificationService;
//
//    @Override
//    public void update(String message, Long userId, Long movieId, NotificationType type) {
//        try {
//            Method method = this.getClass().getDeclaredMethod("handle" + type.name(), String.class, Long.class, Long.class);
//            method.invoke(this, message, userId, movieId);
//        } catch (Exception e) {
//            throw new RuntimeException("No handler for type: " + type.name(), e);
//        }
//    }
//
//    private void handleNEWMOVIE(String message, Long userId, Long movieId) {
//        saveNotification(message, userId, movieId, NotificationType.NEWMOVIE);
//    }
//
//    private void handleNEWREVIEW(String message, Long userId, Long movieId) {
//        saveNotification(message, userId, movieId, NotificationType.NEWREVIEW);
//    }
//
//    private void handleLIKEDREVIEW(String message, Long userId, Long movieId) {
//        saveNotification(message, userId, movieId, NotificationType.LIKEDREVIEW);
//    }
//
//    private void saveNotification(String message, Long userId, Long movieId, NotificationType type) {
//        Notification notification = new Notification(
//                message, type, false, LocalDateTime.now(), userId, movieId
//        );
//        notificationService.saveNotification(notification);
//    }
//}
