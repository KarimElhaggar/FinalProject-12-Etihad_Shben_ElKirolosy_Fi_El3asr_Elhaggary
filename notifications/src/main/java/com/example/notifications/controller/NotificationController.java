package com.example.notifications.controller;

import com.example.notifications.model.Notification;
import com.example.notifications.command.NotificationCommand;
import com.example.notifications.command.NotificationCommandInvoker;
import com.example.notifications.command.SendNotificationCommand;
import com.example.notifications.constants.NotificationType;
import com.example.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/command")
    public ResponseEntity<String> createViaCommand(@RequestBody Notification notification) {
        NotificationCommand command = new SendNotificationCommand(notificationService, notification);
        NotificationCommandInvoker invoker = new NotificationCommandInvoker();
        invoker.addCommand(command);
        invoker.executeAll();
        return ResponseEntity.ok("Notification command executed.");
    }

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
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        Notification updated = notificationService.markAsRead(id);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(404).body(null);
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
