package com.example.movies.observer;

import com.example.movies.model.Movie;
import com.example.movies.constants.NotificationType;

public interface MovieObserver {
    void onMovieUpdated(Movie movie, NotificationType notificationType);
}
