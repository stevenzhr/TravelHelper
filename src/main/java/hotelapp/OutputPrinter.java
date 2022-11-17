package hotelapp;

import hotelapp.hotelDataCollection.Hotel;
import hotelapp.hotelDataCollection.HotelCollection;
import hotelapp.hotelDataCollection.Review;
import hotelapp.hotelDataCollection.ReviewCollection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class OutputPrinter {
    private ReviewCollection reviews;
    private HotelCollection hotels;
    private String fileName;

    public OutputPrinter(HotelCollection hotels, ReviewCollection reviews, String fileName) {
        this.reviews = reviews;
        this.hotels = hotels;
        this.fileName = fileName;
    }

    public void printToFile(boolean isPrintReviews) {
        try {
            File file = new File(fileName);
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            for (Iterator<String> hotelIdIterator = hotels.hotelIdIterator(); hotelIdIterator.hasNext(); ) {
                String hotelId = hotelIdIterator.next();
                Hotel hotel = hotels.getHotel(hotelId);
                fw.write(hotel.toString());
                if (isPrintReviews && (reviews.hotelReviewsIterator(hotelId) != null)) {
                    // Create an iterator for all reviews belongs to the current hotelId.
                    for (Iterator<Review> reviewIterator = reviews.hotelReviewsIterator(hotelId); reviewIterator.hasNext(); ) {
                        Review review = reviewIterator.next();
                        // Call word frequency calculation method and update the main word search map.
                        fw.write(review.toString());
                    }
                }
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
