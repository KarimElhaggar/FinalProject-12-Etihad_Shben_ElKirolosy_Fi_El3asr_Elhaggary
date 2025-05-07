package com.example.movies.repository;

import com.example.movies.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByRatingGreaterThan(double rating);
    List<Movie> findByGenre(String genre);
    List<Movie> findByAuthor(String author);

}
