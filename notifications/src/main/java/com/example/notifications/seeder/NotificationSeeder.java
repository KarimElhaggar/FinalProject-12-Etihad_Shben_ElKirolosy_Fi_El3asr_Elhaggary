//package com.example.notifications.seeder;
//
//import com.example.notifications.constants.NotificationType;
//import com.example.notifications.model.Notification;
//import com.example.notifications.repository.NotificationRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//public class NotificationSeeder implements CommandLineRunner {
//
//    private final NotificationRepository notificationRepo;
//
//    public NotificationSeeder(NotificationRepository notificationRepo) {
//        this.notificationRepo = notificationRepo;
//    }
//
//    @Override
//    public void run(String... args) {
//        if (notificationRepo.count() == 0) {
//            List<Notification> notifications = List.of(
//                    new Notification("Interstellar is now streaming!", NotificationType.LIKEDREVIEW, LocalDateTime.now(), 1L, 100L),
//                    new Notification("The Matrix sequel announced!", NotificationType.NEWMOVIE, LocalDateTime.now(), 2L, 101L),
//                    new Notification("Inception added to your watchlist", NotificationType.NEWREVIEW, LocalDateTime.now(), 3L, 102L),
//                    new Notification("Don't miss Interstellar this weekend!", NotificationType.LIKEDREVIEW, LocalDateTime.now(), 1L, 100L)
//            );
//
//            notificationRepo.saveAll(notifications);
//        }
//    }
//}
