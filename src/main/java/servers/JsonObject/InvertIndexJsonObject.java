package servers.JsonObject;

import hotelapp.hotelDataCollection.Review;

import java.util.ArrayList;
import java.util.List;

public class InvertIndexJsonObject {

    private final Boolean success;
    private final String word;
    private final List<ReviewObject> reviews;

    public InvertIndexJsonObject(Boolean success, String word, List<Review> inputList) {
        this.success = success;
        this.word = word;
        reviews = new ArrayList<>();
        for (Review r:inputList) {
            reviews.add(new ReviewObject(r));
        }
    }

    private class ReviewObject {
        private final String reviewId;
        private final String title;
        private final String user;
        private final String reviewText;
        private final String date;

        public ReviewObject(Review review) {
            this.reviewId = review.getReviewId();
            this.title = review.getTitle();
            this.user = review.getUserNickname();
            this.reviewText = review.getReviewText();
            this.date = review.getSubmissionTime().toString();
        }
    }
}
