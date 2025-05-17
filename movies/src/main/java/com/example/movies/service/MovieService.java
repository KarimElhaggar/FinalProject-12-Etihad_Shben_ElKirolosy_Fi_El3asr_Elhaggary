package com.example.movies.service;

import com.example.movies.constants.NotificationType;
import com.example.movies.model.Movie;
import com.example.movies.rabbitmq.RabbitMQProducer;
import com.example.movies.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Autowired
    public MovieService(MovieRepository movieRepository, RabbitMQProducer rabbitMQProducer) {
        this.movieRepository = movieRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    public List<Movie> getMovies() {
        log.info("Fetching all movies");

        return movieRepository.findAll();
    }

    @CachePut(value = "movie_cache", key = "#movie.id")
    public Movie addMovie(Movie movie) {
        log.info("Adding movie: {}", movie.getMovieName());

        return movieRepository.save(movie);
    }

    @CacheEvict(value = "movie_cache", key = "#id")
    public String deleteMovie(Long id) {
        log.info("Deleting movie with id: {}", id);

        movieRepository.deleteById(id);

        return "Movie deleted successfully!";
    }

    @CachePut(value = "movie_cache", key = "#id")
    public Movie updateMovie(Long id, Movie updatedMovie) {
        log.info("Updating movie with id: {}", id);

        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();

            log.info("Found movie: {}", movie.getMovieName());

            movie.setMovieName(updatedMovie.getMovieName());
            movie.setAuthor(updatedMovie.getAuthor());
            movie.setYearReleased(updatedMovie.getYearReleased());
            movie.setRating(updatedMovie.getRating());
            movie.setGenre(updatedMovie.getGenre());

            if (movie.isReleased() != updatedMovie.isReleased()) {

                log.info("Movie release status changed. Notifying users...");

                rabbitMQProducer.sendToNotifications(movie.getInterestedUserIds(), NotificationType.NEWMOVIE);
            }
            movie.setReleased(updatedMovie.isReleased());

            movieRepository.save(movie);

            log.info("Movie with id {} updated successfully", id);

            return movie;
        } else {

            log.warn("Movie with id {} not found for update", id);

            return null;
        }
    }

    @Cacheable(value = "movie_cache", key = "#id")
    public Movie getMovieById(Long id) {

        log.info("Fetching movie by id: {}", id);

        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()) {

            log.info("Movie found: {}", optionalMovie.get().getMovieName());

            return optionalMovie.get();
        } else {

            log.warn("Movie with id {} not found", id);

            throw new IllegalArgumentException("Movie not found!");
        }
    }

    public List<Movie> getMoviesAboveCertainRating(double rating) {
        log.info("Fetching movies with rating above: {}", rating);

        List<Movie> movies = movieRepository.findByRatingGreaterThan(rating);
        return movies;
    }

    public List<Movie> getMoviesByGenre(String genre) {
        log.info("Fetching movies by genre: {}", genre);

        List<Movie> movies = movieRepository.findByGenre(genre);
        return movies;
    }

    public List<Movie> getMoviesByAuthor(String author) {
        log.info("Fetching movies by author: {}", author);

        List<Movie> movies = movieRepository.findByAuthor(author);
        return movies;
    }

    public List<Movie> getMovieByName(String name) {
        log.info("Fetching movies by name: {}", name);

        List<Movie> movies = movieRepository.findByMovieName(name);
        return movies;
    }

    public List<Movie> getRandomMovies(int limit) {
        log.info("Fetching {} random movies", limit);

        List<Movie> movies = movieRepository.findRandomMovies(limit);
        return movies;
    }

    public void updateMovieRating(Long id, Double rating) {
        log.info("Updating rating for movie id {} to {}", id, rating);

        Movie movie = movieRepository.findById(id).orElse(null);

        if (movie == null) {
            log.warn("Movie with id {} not found for rating update", id);
            throw new IllegalArgumentException("Movie not found!");
        }

        movie.setRating(rating);
        movieRepository.save(movie);

        log.info("Rating updated for movie id {}", id);
    }

    public void addUserToInterestedUserIds(Long movieId, Long userId) {
        log.info("Adding user {} to interested list of movie {}", userId, movieId);

        Movie movie = movieRepository.findById(movieId).orElse(null);

        if (movie == null) {
            log.warn("Movie with id {} not found for adding interested user", movieId);

            throw new IllegalArgumentException("Movie not found!");
        }

        movie.getInterestedUserIds().add(userId);
        movieRepository.save(movie);

        log.info("User {} added to interested list of movie {}", userId, movieId);
    }
}
