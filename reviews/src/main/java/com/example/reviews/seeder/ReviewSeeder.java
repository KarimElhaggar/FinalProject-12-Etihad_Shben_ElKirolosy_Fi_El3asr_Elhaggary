//package com.example.reviews.seeder;
//
//import com.example.reviews.constants.ReviewStatus;
//import com.example.reviews.model.Review;
//import com.example.reviews.repository.ReviewRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class ReviewSeeder implements CommandLineRunner {
//
//    private final ReviewRepository reviewRepo;
//
//    public ReviewSeeder(ReviewRepository reviewRepo) {
//        this.reviewRepo = reviewRepo;
//    }
//
//    @Override
//    public void run(String... args) {
//        if (reviewRepo.count() == 0) {
//            List<Review> reviews = List.of(
//                    new Review(5.0, 10L, ReviewStatus.APPROVED, "Amazing visuals!", 1L, 100L),
//                    new Review(4.5, 7L, ReviewStatus.APPROVED, "Very thought-provoking.", 2L, 101L),
//                    new Review(4.0, 3L, ReviewStatus.PENDING, "Nice concept, a bit confusing.", 3L, 102L),
//                    new Review(3.8, 2L, ReviewStatus.APPROVED, "Good movie, but overhyped.", 1L, 101L)
//            );
//
//            reviewRepo.saveAll(reviews);
//        }
//    }
//}
