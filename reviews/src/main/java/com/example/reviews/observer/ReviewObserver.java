package com.example.reviews.observer;

import com.example.reviews.constants.NotificationType;

import java.util.List;

public interface ReviewObserver {
    void onReviewEvent(List<Long> userIds, NotificationType type);
}
