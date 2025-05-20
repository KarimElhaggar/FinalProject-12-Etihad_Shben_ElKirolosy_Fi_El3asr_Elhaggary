package com.example.movies.service;

import com.example.movies.constants.NotificationType;
import com.example.movies.model.Movie;
import com.example.movies.observer.MoviePublisher;
import com.example.movies.rabbitmq.RabbitMQProducer;
import com.example.movies.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MovieService {

    private final MovieRepository movieRepository;
    //private final RabbitMQProducer rabbitMQProducer;
    private final MoviePublisher moviePublisher;


    @Autowired
    public MovieService(MovieRepository movieRepository, MoviePublisher moviePublisher) {
        this.movieRepository = movieRepository;
       // this.rabbitMQProducer = rabbitMQProducer;
        this.moviePublisher = moviePublisher;
    }

    public List<Movie> getMovies() {
        log.info("Fetching all movies");

        return movieRepository.findAll();
    }

    public void seed() {
        log.info("seeding movies");

        if (movieRepository.count() == 0) {
            List<Movie> movies = List.of(
                    new Movie.Builder()
                            .movieName("Interstellar")
                            .author("Christopher Nolan")
                            .yearReleased(2014)
                            .rating(4.5)
                            .genre("Sci-Fi")
                            .released(true)
                            .interestedUserIds(List.of(1L, 2L))
                            .build(),
                    new Movie.Builder()
                            .movieName("The Matrix")
                            .author("The Wachowskis")
                            .yearReleased(1999)
                            .rating(4.7)
                            .genre("Action")
                            .released(true)
                            .interestedUserIds(List.of(2L, 3L))
                            .build(),
                    new Movie.Builder()
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

    @CachePut(value = "movie_cache", key = "#movie.id")
    public Movie addMovie(Movie movie) {
        log.info("Adding movie: {}", movie.getMovieName());

        return movieRepository.save(movie);
    }

    @CacheEvict(value = "movie_cache", key = "#id")
    public String deleteMovie(Long id) {
        log.info("Deleting movie with id: {}", id);

        if (!movieRepository.existsById(id)) {
            log.warn("Movie with id {} not found for deletion", id);
            return "Movie not found!";
        }

        movieRepository.deleteById(id);
        log.info("Movie with id {} deleted successfully", id);

        return "Movie deleted successfully!";
    }

    @CachePut(value = "movie_cache", key = "#id")
    public Movie updateMovie(Long id, Movie updatedMovie) {
        log.info("Updating movie with id: {}", id);

        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();
            log.info("Found movie: {}", movie.getMovieName());

            // Update fields only if they are provided in updatedMovie
            if (updatedMovie.getMovieName() != null) {
                movie.setMovieName(updatedMovie.getMovieName());
            }
            if (updatedMovie.getAuthor() != null) {
                movie.setAuthor(updatedMovie.getAuthor());
            }
            if (updatedMovie.getYearReleased() != null) {
                movie.setYearReleased(updatedMovie.getYearReleased());
            }
            if (updatedMovie.getRating() != null) {
                movie.setRating(updatedMovie.getRating());
            }
            if (updatedMovie.getGenre() != null) {
                movie.setGenre(updatedMovie.getGenre());
            }

            // Handle the release status carefully (partial update)
            if (updatedMovie.isReleased() != movie.isReleased()) {
                log.info("Movie release status changed. Notifying users...");
                movie.setReleased(updatedMovie.isReleased());
                moviePublisher.notifyObservers(movie, NotificationType.NEWMOVIE);
            }

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

            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Movie not found!");
    
        }
    }

    public boolean movieExists(Long id) {
        return movieRepository.existsById(id);
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

    @CacheEvict(value = "movie_cache", key = "#id")
    public void updateMovieRating(Long id, Double rating) {
        log.info("Updating rating for movie id {} to {}", id, rating);

        Movie movie = movieRepository.findById(id).orElse(null);

        if (movie == null) {
            log.warn("Movie with id {} not found for rating update", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found!");
        }

        movie.setRating(rating);
        movieRepository.save(movie);

        log.info("Rating updated for movie id {}", id);
    }

    @CacheEvict(value = "movie_cache", key = "#movieId")
    public void addUserToInterestedUserIds(Long movieId, Long userId) {
        log.info("Adding user {} to interested list of movie {}", userId, movieId);

        Movie movie = movieRepository.findById(movieId).orElse(null);

        if (movie == null) {
            log.warn("Movie with id {} not found for adding interested user", movieId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found!");
        }

//        movie.getInterestedUserIds().add(userId);
        movie.addInterestedUserId(userId);
        movieRepository.save(movie);

        log.info("User {} added to interested list of movie {}", userId, movieId);
    }
}
