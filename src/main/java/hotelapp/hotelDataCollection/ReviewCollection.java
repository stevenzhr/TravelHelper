package hotelapp.hotelDataCollection;

import hotelapp.JsonFileParser;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class ReviewCollection {
    // properties
    private final Map<String, TreeSet<Review>> reviewCollection;  // String -> hotelId, TreeSet -> reviews by date

    /**
     * class constructor with argument of reviewDirectory.
     * @param reviewsDirectory review's json file directory
     */
    public ReviewCollection(String reviewsDirectory) {
        JsonFileParser jsonFileParser = new JsonFileParser();
        reviewCollection = jsonFileParser.traverseDirectory(reviewsDirectory);

    }

    /**
     * Return an iterator of hotel ID in reviews HashMap
     * @return Iterator of hotel ID
     */
    public Iterator<String> hotelIdIterator() {
        return reviewCollection.keySet().iterator();
    }

    /**
     * Return an iterator of given hotel's reviews collection.
     * @param hotelId given hotel ID
     * @return Iterator of Review
     */
    public Iterator<Review> hotelReviewsIterator(String hotelId) {
        if (reviewCollection.get(hotelId) == null)
            return null;
        else {
            return reviewCollection.get(hotelId).iterator();
        }
    }
}
