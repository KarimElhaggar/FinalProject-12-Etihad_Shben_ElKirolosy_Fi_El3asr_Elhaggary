//package com.example.notifications.config;
//
//import com.example.notifications.constants.NotificationType;
//import com.example.notifications.model.Notification;
//import com.example.notifications.repository.NotificationRepository;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//public class NotificationsSeederConfig {
//
//    @Autowired
//    private NotificationRepository notificationRepository;
//
//    @PostConstruct
//    public void seedOnStartup() {
//        if (notificationRepository.count() == 0) {
//            List<Notification> notifications = List.of(
//                    new Notification("Interstellar is now streaming!", NotificationType.LIKEDREVIEW, false, 1L, 100L),
//                    new Notification("The Matrix sequel announced!", NotificationType.NEWMOVIE, false, 2L, 101L),
//                    new Notification("Inception added to your watchlist", NotificationType.NEWREVIEW, false, 3L, 102L),
//                    new Notification("Don't miss Interstellar this weekend!", NotificationType.LIKEDREVIEW, false, 1L, 100L)
//            );
//
//            notificationRepository.saveAll(notifications);
//        }
//    }
//}
