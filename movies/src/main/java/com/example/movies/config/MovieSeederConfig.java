package com.example.movies.config;

import com.example.movies.model.Movie;
import com.example.movies.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MovieSeederConfig {

    @Autowired
    private MovieRepository movieRepository;

    @PostConstruct
    public void seedOnStartup() {
        if (movieRepository.count() == 0) {
            List<Movie> movies = List.of(
                    new Movie.Builder()
                            .id(100L)
                            .movieName("Interstellar")
                            .author("Christopher Nolan")
                            .yearReleased(2014)
                            .rating(4.5)
                            .genre("Sci-Fi")
                            .released(true)
                            .interestedUserIds(List.of(1L, 2L))
                            .build(),
                    new Movie.Builder()
                            .id(101L)
                            .movieName("The Matrix")
                            .author("The Wachowskis")
                            .yearReleased(1999)
                            .rating(4.7)
                            .genre("Action")
                            .released(true)
                            .interestedUserIds(List.of(2L, 3L))
                            .build(),
                    new Movie.Builder()
                            .id(102L)
                            .movieName("Inception")
                            .author("Christopher Nolan")
                            .yearReleased(2010)
                            .rating(4.6)
                            .genre("Thriller")
                            .released(true)
                            .interestedUserIds(List.of(1L, 3L))
                            .build()
            );

            movieRepository.saveAll(movies);
        }
    }
}
