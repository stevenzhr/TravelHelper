package servers.JsonObject;

import hotelapp.hotelDataCollection.Review;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReviewJsonObject{
    private final Boolean success;
    private final String hotelId;
    private final List<ReviewObject> reviews;

    public ReviewJsonObject(Boolean success, String hotelId, int num, Iterator<Review> it) {
//        super(success, hotelId);
        this.success = success;
        this.hotelId = hotelId;
        reviews = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if (it.hasNext()) {
                reviews.add(new ReviewObject(it.next()));
            }
            else
                break;
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
