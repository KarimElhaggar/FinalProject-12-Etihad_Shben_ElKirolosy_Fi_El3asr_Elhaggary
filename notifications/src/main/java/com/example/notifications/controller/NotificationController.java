package com.example.notifications.controller;

import com.example.notifications.model.Notification;
import com.example.notifications.constants.NotificationType;
import com.example.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/observer")
    public String createViaObserver(@RequestParam String message,
                                                    @RequestParam Long userId,
                                                    @RequestParam NotificationType type,
                                                    @RequestParam(required = false) Long movieId) {
        notificationService.triggerObserverNotification(message, userId, movieId, type);
        return "Notification sent via observer.";
    }
//todo
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {

        return notificationService.saveNotification(notification);
    }

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable Long userId) {
        //TODO check if user exists
        return notificationService.getNotificationsByUserId(userId);
    }

    @GetMapping("/user/{userId}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable Long userId) {
        return notificationService.getUnreadNotificationsByUserId(userId);
    }

    @GetMapping("/user/{userId}/unread/{type}")
    public List<Notification> getUnreadByType(@PathVariable Long userId,
                                                              @PathVariable NotificationType type) {
        return notificationService.getUnreadByType(userId, type);
    }

    @GetMapping("/{id}")
    public Notification getNotificationById(@PathVariable String id) {
        return notificationService.getNotificationById(id);
    }

    @PutMapping("/{id}/read")
    public String markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return "Read status updated.";
    }
//todo
    @PutMapping("/{id}/unread")
    public String markAsUnread(@PathVariable String id) {
        notificationService.markAsUnread(id);
        return "Read status updated.";
    }

    @PostMapping("/send")
    public String sendBatch(@RequestBody Long[] ids, @RequestParam("NotificationType") NotificationType type) {
        notificationService.sendBatch(ids, type);
        return "Notifications sent.";
    }

    @DeleteMapping("/{id}")
    public String deleteNotification(@PathVariable String id) {
        //TODO check if noti exists
        notificationService.deleteNotification(id);
        return "Notification deleted";
    }

    @DeleteMapping("/user/{userId}")
    public String deleteAllByUser(@PathVariable Long userId) {
        //TODO check if user exists
        notificationService.deleteAllByUserId(userId);
        return "All notifications for user deleted";
    }

    private static final Set<String> ALLOWED_METHODS = Set.of(
            "getNotificationType", "isMarkAsRead", "getMovieId"
    );
//todo
    @GetMapping("/filter")
    public List<Notification> filterByMethod(
            @RequestParam String method,
            @RequestParam String value) {

        if (!ALLOWED_METHODS.contains(method)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Method not allowed");
        }

        try {
            Method m = Notification.class.getMethod(method);
            Class<?> returnType = m.getReturnType();
            Object typedValue;

            if (returnType == Long.class || returnType == long.class) {
                typedValue = Long.parseLong(value);
            } else if (returnType == Boolean.class || returnType == boolean.class) {
                typedValue = Boolean.parseBoolean(value);
            } else if (returnType.isEnum()) {
                typedValue = Enum.valueOf((Class<Enum>) returnType, value.toUpperCase());
            } else {
                typedValue = value;
            }

            List<Notification> result = notificationService.filterNotificationsBy(method, typedValue);
            return result;
        } catch (NoSuchMethodException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Method not allowed");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "STOP RUINoivcnveroivn");
        }
    }
}
