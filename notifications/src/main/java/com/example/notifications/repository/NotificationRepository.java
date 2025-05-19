package com.example.notifications.repository;

import com.example.notifications.model.Notification;
import com.example.notifications.constants.NotificationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndMarkAsReadFalse(Long userId);

    @Query("{'userId': ?0, 'notificationType': ?1, 'markAsRead': false }")
    List<Notification> findUnreadByType(Long userId, NotificationType type);

    void deleteByUserId(Long userId);
}
