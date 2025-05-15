package com.example.movies.service;

import com.example.movies.constants.NotificationType;
import com.example.movies.model.Movie;
import com.example.movies.rabbitmq.RabbitMQProducer;
import com.example.movies.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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
       return movieRepository.findAll();

    }

    public String addMovie(Movie movie) {
        movieRepository.save(movie);
        return "Movie added successfully!";
    }

    public String deleteMovie(Long id) {
        movieRepository.deleteById(id);
        return "Movie deleted successfully!";
    }

    public String updateMovie(Long id, Movie updatedMovie) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();
            movie.setMovieName(updatedMovie.getMovieName());
            movie.setAuthor(updatedMovie.getAuthor());
            movie.setYearReleased(updatedMovie.getYearReleased());
            movie.setRating(updatedMovie.getRating());
            movie.setGenre(updatedMovie.getGenre());

            if(movie.isReleased() != updatedMovie.isReleased()) {
                //add logic to handle the change in release status

                rabbitMQProducer.sendToNotifications(movie.getInterestedUserIds(), NotificationType.NEWMOVIE);
            }
            movie.setReleased(updatedMovie.isReleased());


            movieRepository.save(movie);
            return "Movie updated successfully!";
        } else {
            return "Movie not found!";
        }
    }

    public Movie getMovieById(Long id) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()) {
            return optionalMovie.get();
        } else {
            throw new IllegalArgumentException("Movie not found!");
        }
    }

    public List<Movie> getMoviesAboveCertainRating(double rating) {
        List<Movie> movies = movieRepository.findByRatingGreaterThan(rating);
        return movies;
    }

    public List<Movie> getMoviesByGenre(String genre) {
        List<Movie> movies = movieRepository.findByGenre(genre);
        return movies;
    }

    public List<Movie> getMoviesByAuthor(String author) {
        List<Movie> movies = movieRepository.findByAuthor(author);
        return movies;
    }

    public List<Movie> getMovieByName(String name) {
        List<Movie> movies = movieRepository.findByMovieName(name);
        return movies;
    }

    public List<Movie> getRandomMovies(int limit) {
        List<Movie> movies = movieRepository.findRandomMovies(limit);
        return movies;
    }
}
