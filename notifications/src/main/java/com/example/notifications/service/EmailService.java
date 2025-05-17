package com.example.notifications.service;

import com.example.notifications.constants.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotificationEmail(String to, NotificationType type) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setTo(to);
        mailMessage.setSubject(getSubjectForType(type));
        mailMessage.setText(getBodyForType(type));

        mailSender.send(mailMessage);
    }

    private String getSubjectForType(NotificationType type) {
        return switch (type) {
            case NEWMOVIE -> "New Movie Alert!";
            case NEWREVIEW -> "New Review Posted!";
            case LIKEDREVIEW -> "Your Review Got a Like!";
        };
    }

    private String getBodyForType(NotificationType type) {
        return switch (type) {
            case NEWMOVIE -> "Check out the newly added movie!\n\n";
            case NEWREVIEW -> "A new review just dropped. Read now!\n\n";
            case LIKEDREVIEW -> "Good news! Someone liked your review.\n\nView it now!\n";
        };
    }

}