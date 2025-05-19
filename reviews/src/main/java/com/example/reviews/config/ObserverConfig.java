package com.example.reviews.config;

import com.example.reviews.observer.ReviewObserver;
import com.example.reviews.observer.ReviewPublisher;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Configuration
public class ObserverConfig {

    private final ReviewPublisher publisher;
    private final List<ReviewObserver> observers;

    public ObserverConfig(ReviewPublisher publisher, List<ReviewObserver> observers) {
        this.publisher = publisher;
        this.observers = observers;
    }

    @PostConstruct
    public void init() {
        observers.forEach(publisher::subscribe);
    }
}