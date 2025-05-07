package com.example.movies.service;

 import com.example.movies.model.Movie;
 import com.example.movies.repository.MovieRepository;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Service;
 import java.util.List;
 import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public String getMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.toString();
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

                //notification logic
            }
            movie.setReleased(updatedMovie.isReleased());


            movieRepository.save(movie);
            return "Movie updated successfully!";
        } else {
            return "Movie not found!";
        }
    }

    public String getMovieById(Long id) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()) {
            return optionalMovie.get().toString();
        } else {
            return "Movie not found!";
        }
    }

    public String getMoviesAboveCertainRating(double rating) {
        List<Movie> movies = movieRepository.findByRatingGreaterThan(rating);
        return movies.toString();
    }

    public String getMoviesByGenre(String genre) {
        List<Movie> movies = movieRepository.findByGenre(genre);
        return movies.toString();
    }

    public String getMoviesByAuthor(String author) {
        List<Movie> movies = movieRepository.findByAuthor(author);
        return movies.toString();
    }

}
