package com.example.notifications.service;

import com.example.notifications.command.CreateNotificationCommand;
import com.example.notifications.command.NotificationCommand;
import com.example.notifications.command.SendNotificationCommand;
import com.example.notifications.command.ToggleReadCommand;
import com.example.notifications.constants.NotificationType;
import com.example.notifications.feign.RemoteUserService;
import com.example.notifications.model.Notification;
//import com.example.notifications.observer.NotificationPublisher;
//import com.example.notifications.observer.NotificationSubscriber;
import com.example.notifications.observer.NotificationObserver;
import com.example.notifications.rabbitmq.RabbitMQConfig;
import com.example.notifications.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class NotificationService implements NotificationObserver {
    private final RemoteUserService remoteUserService;

    @Autowired
    private final EmailService emailService;
    @Autowired
    private NotificationRepository notificationRepository;
//    @Autowired
//    private NotificationPublisher publisher;
//    @Autowired
//    private NotificationSubscriber subscriber;
    @Autowired
    private final NotificationCommandInvoker invoker;

    @Override
    public void onNotificationReceived(List<Long> userIds, NotificationType type) {
        // Delegate to the existing sendBatch method
        Long[] userIdsArray = userIds.toArray(new Long[0]);
        sendBatch(userIdsArray, type);
    }
    public NotificationService(RemoteUserService remoteUserService, NotificationRepository notificationRepository,
                               NotificationCommandInvoker invoker,
                               EmailService emailService
    ) {
        this.remoteUserService = remoteUserService;
        this.notificationRepository = notificationRepository;
//        this.publisher = publisher;
//        this.subscriber = subscriber;
        this.invoker = invoker;
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        log.info("PostConstruct init(): Subscribing subscriber to publisher.");
//        publisher.subscribe(subscriber);
    }

    public Notification saveNotification(Notification notification) {
        log.info("Saving notification for userId: {}", notification.getUserId());
        if(notification.getUserId() == null) {
            log.error("User ID is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is null");
        }
        if(notification.getNotificationType() == null) {
            log.error("Notification type is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification type is null");
        }
        if(notification.getNotification() == null) {
            log.error("Notification message is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification message is null");
        }
        if(notification.getNotificationDate()==null){
            //add todays date
            notification.setNotificationDate(LocalDateTime.now());
        }
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        log.info("Fetching all notifications");

        return notificationRepository.findAll();
    }

    public void seed() {
        log.info("seeding notifications");

        if (notificationRepository.count() == 0) {
            List<Notification> notifications = List.of(
                    new Notification("Interstellar is now streaming!", NotificationType.LIKEDREVIEW, false, 1L, 100L),
                    new Notification("The Matrix sequel announced!", NotificationType.NEWMOVIE, false, 2L, 101L),
                    new Notification("Inception added to your watchlist", NotificationType.NEWREVIEW, false, 3L, 102L),
                    new Notification("Don't miss Interstellar this weekend!", NotificationType.LIKEDREVIEW, false, 1L, 100L)
            );

            notificationRepository.saveAll(notifications);
        }
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        log.info("Fetching notifications for userId: {}", userId);

        return notificationRepository.findByUserId(userId);
    }

    public Notification getNotificationById(String id) {
        log.info("Fetching notification by id: {}", id);

        Optional<Notification> optional = notificationRepository.findById(id);
        if (optional.isPresent()) {

            log.info("Checking if notification {} is already marked as read", id);
            Notification notification = optional.get();
            if (!notification.isMarkAsRead()) {

                log.info("Marking notification {} as read", id);

                notification.setMarkAsRead(true);
                notificationRepository.save(notification);


            }
            return notification;
        }else{
            log.info("No notification found for id {}", id);

            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Notification not found");
        }


    }

    public void markAsRead(String id) {
        log.info("Marking notification {} as read", id);
        Notification n = notificationRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        NotificationCommand command = new ToggleReadCommand(n, true);
        invoker.setCommand(command);
        invoker.executeCommand();

        notificationRepository.save(n);

        log.info("Notification {} marked as read and saved", id);
    }

    public void markAsUnread(String id) {
        log.info("Marking notification {} as unread", id);

        Notification n = notificationRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        NotificationCommand command = new ToggleReadCommand(n, false);
        invoker.setCommand(command);
        invoker.executeCommand();
        notificationRepository.save(n);

        log.info("Notification {} marked as unread and saved", id);
    }
//
//    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
//    public void sendBatch(String message) {
//        log.info("Received message on RabbitMQ queue: {}", message);
//        String[] arguments = message.split(";");
//
//        List<Long> ids = Arrays.stream(arguments[0].split(","))
//                .map(Long::parseLong)
//                .toList();
//
//        NotificationType type = arguments[1].equals(NotificationType.NEWMOVIE.toString())
//                ? NotificationType.NEWMOVIE
//                : arguments[1].equals(NotificationType.NEWREVIEW.toString())
//                ? NotificationType.NEWREVIEW
//                : NotificationType.LIKEDREVIEW;
//
//        //System.out.println("Received a notification of type" + type);
//        log.info("Parsed notification type: {}. Sending to userIds: {}", type, ids);
//
//        sendBatch(ids, type);
//    }

    public void sendBatch(Long[] ids, NotificationType type) {
        log.info("Sending batch notifications of type {} to userIds: {}", type, ids);
        List<String> mails = new ArrayList<>();
        for (Long userId : ids) {
            NotificationCommand created = new CreateNotificationCommand(this, type, userId);
            invoker.addCommand(created);
            try {
                String to = remoteUserService.getUserEmailById(userId);
                mails.add(to);
              emailService.sendNotificationEmail(to, type);
            } catch (Exception e) {
                log.error("Failed to send email to userId {}: {}", userId, e.getMessage(), e);
            }
        }
        NotificationCommand command = new SendNotificationCommand(mails, type, emailService);
        invoker.executeAll();

        log.info("Finished executing batch notification commands for type {}", type);
    }

    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        log.info("Fetching unread notifications for userId: {}", userId);
        return notificationRepository.findByUserIdAndMarkAsReadFalse(userId);
    }

    public List<Notification> getUnreadByType(Long userId, NotificationType type) {
        log.info("Fetching unread notifications for userId: {} and type: {}", userId, type);
        return notificationRepository.findUnreadByType(userId, type);
    }

    public void deleteNotification(String id) {
        log.info("Deleting notification with id: {}", id);
        notificationRepository.deleteById(id);
    }

    public void deleteAllByUserId(Long userId) {
        log.info("Deleting all notifications for userId: {}", userId);
        notificationRepository.deleteByUserId(userId);
    }

    public void triggerObserverNotification(String message, Long userId, Long movieId, NotificationType type) {
        log.info("Triggering observer notification with message: '{}', userId: {}, movieId: {}, type: {}", message, userId, movieId, type);
        //publisher.notifyObservers(message, userId, movieId, type);
    }

    public List<Notification> filterNotificationsBy(String methodName, Object expectedValue) {
        log.info("Filtering notifications by method: {} with expectedValue: {}", methodName, expectedValue);
        List<Notification> allNotifications = notificationRepository.findAll();
        List<Notification> filtered = new ArrayList<>();

        for (Notification notification : allNotifications) {
            try {
                Method method = notification.getClass().getMethod(methodName);
                Object result = method.invoke(notification);
                if (Objects.equals(result, expectedValue)) {
                    filtered.add(notification);
                }
            } catch (Exception e) {
                //System.err.println("Reflection error: " + e.getMessage());
                log.error("Reflection error {}", e.getMessage(), e);
            }
        }

        log.info("Found {} matching notifications after filter", filtered.size());
        return filtered;
    }

}

