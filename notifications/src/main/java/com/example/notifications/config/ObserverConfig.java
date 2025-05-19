package com.example.notifications.config;
import com.example.notifications.observer.NotificationObserver;
import com.example.notifications.observer.NotificationPublisher;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ObserverConfig {

    @Autowired
    private NotificationPublisher publisher;

    @Autowired
    private List<NotificationObserver> observers;

    @PostConstruct
    public void registerObservers() {
        observers.forEach(publisher::subscribe);
    }
}
