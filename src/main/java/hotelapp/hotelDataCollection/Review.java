package hotelapp.hotelDataCollection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Review implements Comparable<Review>{
    // Constants
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    // Properties
    private final String reviewId;
    private final String hotelId;
    private final int ratingOverall;
    private final String title;
    private final String reviewText;
    private final String userNickname;
    private final LocalDate submissionTime;


    /** Static nested helper class for builder*/
    public static class Builder {
        // essential parameters
        private final String reviewId, reviewText;
        // optional parameters
        private String hotelId = "";
        private int ratingOverall = 0;
        private String title = "";
        private String userNickname = "";
        private LocalDate submissionTime = LocalDate.of(1900, 1, 1);

        /**
         * Builder's constructor
         * @param reviewId review ID
         * @param reviewText review text
         */
        public Builder (String reviewId, String reviewText) {
            this.reviewId = reviewId;
            this.reviewText = reviewText;
        }

        /**
         * Setter for hotelId
         * @param hotelId hotel ID
         * @return Instance of class Builder
         */
        public Builder hotelId(String hotelId) {
            this.hotelId = hotelId;
            return this;
        }

        /**
         * Setter for rating overall
         * @param ratingOverall rating overall
         * @return Instance of class Builder
         */
        public Builder ratingOverall(int ratingOverall) {
            this.ratingOverall = ratingOverall;
            return this;
        }

        /**
         * Setter for review title
         * @param title review title
         * @return Instance of class Builder
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Setter for review user nickname
         * @param userNickname user nickname
         * @return Instance of class Builder
         */
        public Builder userNickname(String userNickname) {
            this.userNickname = userNickname;
            return this;
        }

        /**
         * Setter for review submission time. Input parameter is a String. Which up to the pattern of
         * "yyyy-MM-dd'T'HH:mm:ss'Z'"
         * @param submissionTime review submission time in String.
         * @return Instance of class Builder
         */
        public Builder submissionTime(String submissionTime) {
            // data type validation
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            try {
                this.submissionTime = LocalDate.parse(submissionTime, formatter);
            }catch ( DateTimeParseException e) {
                this.submissionTime = LocalDate.parse("1900-01-01T00:00:00Z", formatter);
            }
            return this;
        }

        /**
         * Constructs an instance of class Review
         * @return a new instance of class Review
         */
        public Review build() {
            // TODO: Decide What kind of Data validation can implement here.
            if (userNickname.equals(""))
                this.userNickname = "Anonymous";
            return new Review(this);
        }
    }// End of static nested class---------

    /**
     * Constructor for class review with builder.
     * @param builder instance of Builder
     */
    private Review(Builder builder){
        this.reviewId       = builder.reviewId;
        this.hotelId        = builder.hotelId;
        this.ratingOverall  = builder.ratingOverall;
        this.title          = builder.title;
        this.reviewText     = builder.reviewText;
        this.userNickname   = builder.userNickname;
        this.submissionTime = builder.submissionTime;
    }

    public Review(Review review) {
        this.reviewId       = review.reviewId;
        this.hotelId        = review.hotelId;
        this.ratingOverall  = review.ratingOverall;
        this.title          = review.title;
        this.reviewText     = review.reviewText;
        this.userNickname   = review.userNickname;
        this.submissionTime = review.submissionTime;
    }

    /**
     * Getter for ReviewId
     * @return review ID
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * Getter for HotelId
     * @return hotel ID
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Getter for ReviewText
     * @return review text
     */
    public String getReviewText() {
        return reviewText;
    }

    public String getTitle() {
        return title;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public LocalDate getSubmissionTime() {
        return submissionTime;
    }

    /**
     * Output review's details.
     * @return String of review's details.
     */


    @Override
    public String toString() {
        return
                "--------------------" + System.lineSeparator() +
                "Review by " + userNickname + " on " + submissionTime + System.lineSeparator() +
                        "Rating: " + ratingOverall + System.lineSeparator() +
                        "ReviewId: " + reviewId + System.lineSeparator() +
                        title + System.lineSeparator() +
                        reviewText + System.lineSeparator();
    }

    @Override
    public int compareTo(Review o) {
        if(!this.submissionTime.equals(o.submissionTime))
            return -this.submissionTime.compareTo(o.submissionTime);
        else
            return this.reviewId.compareTo(o.reviewId);
    }

}
