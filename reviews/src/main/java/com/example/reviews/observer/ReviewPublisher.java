package com.example.reviews.observer;

import com.example.reviews.constants.NotificationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewPublisher {

    private final List<ReviewObserver> observers = new ArrayList<>();

    public void subscribe(ReviewObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(ReviewObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(List<Long> userIds, NotificationType type) {
        for (ReviewObserver observer : observers) {
            observer.onReviewEvent(userIds, type);
        }
    }
}
