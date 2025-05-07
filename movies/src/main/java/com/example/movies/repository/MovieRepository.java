package com.example.movies.repository;

import com.example.movies.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByRatingGreaterThan(double rating);
    List<Movie> findByGenre(String genre);
    List<Movie> findByAuthor(String author);
    List<Movie> findByMovieName(String name);
    @Query(value = "SELECT * FROM movies ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Movie> findRandomMovies(@Param("limit") int limit);}
