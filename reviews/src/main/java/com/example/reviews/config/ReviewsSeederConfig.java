//package com.example.reviews.config;
//
//import com.example.reviews.constants.ReviewStatus;
//import com.example.reviews.model.Review;
//import com.example.reviews.repository.ReviewRepository;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//public class ReviewsSeederConfig {
//
//    @Autowired
//    private ReviewRepository reviewRepository;
//
//    @PostConstruct
//    public void seedOnStartup(){
//        if (reviewRepository.count() == 0) {
//            List<Review> reviews = List.of(
//                    new Review(5.0, 10L, ReviewStatus.APPROVED, "Amazing visuals!", 1L, 100L),
//                    new Review(4.5, 7L, ReviewStatus.APPROVED, "Very thought-provoking.", 2L, 101L),
//                    new Review(4.0, 3L, ReviewStatus.PENDING, "Nice concept, a bit confusing.", 3L, 102L),
//                    new Review(3.8, 2L, ReviewStatus.APPROVED, "Good movie, but overhyped.", 1L, 101L)
//            );
//
//            reviewRepository.saveAll(reviews);
//        }
//    }
//}
