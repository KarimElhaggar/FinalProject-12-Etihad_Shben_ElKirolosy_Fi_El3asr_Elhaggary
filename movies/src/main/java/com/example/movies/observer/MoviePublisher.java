package com.example.movies.observer;

import com.example.movies.model.Movie;
import com.example.movies.constants.NotificationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MoviePublisher {

    private final List<MovieObserver> observers = new ArrayList<>();

    public void subscribe(MovieObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(MovieObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Movie movie, NotificationType type) {
        for (MovieObserver observer : observers) {
            observer.onMovieUpdated(movie, type);
        }
    }
}
