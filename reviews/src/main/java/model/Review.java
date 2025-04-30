package model;

import constants.ReviewStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Document("reviews")
public class Review {
    @Id
    private String id;
    private Double rating;
    private Long likesCount;
    private ReviewStatus status;
    private String reviewDescription;
    private Integer userId;
    private Integer movieId;

    public Review(Double rating, Long likesCount, ReviewStatus status, String reviewDescription, Integer userId, Integer movieId) {
        this.rating = rating;
        this.likesCount = likesCount;
        this.status = status;
        this.reviewDescription = reviewDescription;
        this.userId = userId;
        this.movieId = movieId;
    }
}
