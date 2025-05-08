package com.example.notifications.controller;

import com.example.notifications.model.Notification;
import com.example.notifications.constants.NotificationType;
import com.example.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> createViaObserver(@RequestParam String message,
                                                    @RequestParam Long userId,
                                                    @RequestParam NotificationType type,
                                                    @RequestParam(required = false) Long movieId) {
        notificationService.triggerObserverNotification(message, userId, movieId, type);
        return ResponseEntity.ok("Notification sent via observer.");
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.saveNotification(notification));
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserId(userId));
    }

    @GetMapping("/user/{userId}/unread/{type}")
    public ResponseEntity<List<Notification>> getUnreadByType(@PathVariable Long userId,
                                                              @PathVariable NotificationType type) {
        return ResponseEntity.ok(notificationService.getUnreadByType(userId, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        Optional<Notification> optional = notificationService.getNotificationById(id);
        return optional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(null));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Read status updated.");
    }

    @PutMapping("/{id}/unread")
    public ResponseEntity<?> markAsUnread(@PathVariable Long id) {
        notificationService.markAsUnread(id);
        return ResponseEntity.ok("Read status updated.");
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendBatch(@RequestBody List<Long> ids) {
        notificationService.sendBatch(ids);
        return ResponseEntity.ok("Notifications sent.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted");
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteAllByUser(@PathVariable Long userId) {
        notificationService.deleteAllByUserId(userId);
        return ResponseEntity.ok("All notifications for user deleted");
    }
}
