package hotelapp;

import hotelapp.hotelDataCollection.*;

import java.util.Map;

public class AppInitializer {
    private Map<String, String> commandLineArgsMap;
    private int nThreads;
    public final boolean isParseReview;

    public AppInitializer(Map<String, String> commandLineArgsMap) {
        this.commandLineArgsMap = commandLineArgsMap;
        this.isParseReview = commandLineArgsMap.containsKey("-reviews");
        checkThreadsValidity();
    }

    /**
     * Check whether command argument has "-threads" or not and the parameter is whether larger than 0.
     * If failed, set threads to 1.
     */
    private void checkThreadsValidity() {
        if (!commandLineArgsMap.containsKey("-threads") || Integer.parseInt(commandLineArgsMap.get("-threads")) < 1)
            nThreads = 1;
        else
            nThreads = Integer.parseInt(commandLineArgsMap.get("-threads"));
    }

    /**
     * Create a thread safe hotel collection by given input -hotel path. For this project, the thread number
     * of hotel parser is 1.
     * @return HotelCollection
     */
    public HotelCollection iniHotelCollection() {
        return new ThreadSafeHotelCollection(commandLineArgsMap.get("-hotels"), 1);
    }

    /**
     * Check command argument has "-reviews" or not. If yes, create a thread safe review collection by given
     * input -reviews path. If not, return null.
     * @return ReviewCollection with valid "-reviews" or null without "-reviews"
     */
    public ReviewCollection iniReviewCollection() {
        if (!commandLineArgsMap.containsKey("-reviews"))
            return null;
        else
            return new ThreadSafeReviewCollection(commandLineArgsMap.get("-reviews"), nThreads);
    }

    /**
     * Calculate inverted index by review collection and path of stop word list. If input review collection is
     * null then return null.
     * @param reviewCollection Raw review data collection
     * @param stopWords path of stop word list
     * @return InvertedIndex with valid review collection or null with null review collection
     */
    public InvertedIndex iniInvertedIndex(ReviewCollection reviewCollection, String stopWords) {
        if (reviewCollection == null)
            return null;
        else {
            InvertedIndex ThreadSafeInvertedIndex = new ThreadSafeInvertedIndex(stopWords);
            ThreadSafeInvertedIndex.processWord(reviewCollection);
            return ThreadSafeInvertedIndex;
        }
    }

    /**
     * Return a fusion hotel and review data collection that contain all hotels/reviews/invertIndex data
     * in a single class.
     * @param stopWords path of stop word list
     * @return fusion hotel data
     */
    public FusionDataCollection getFusionData(String stopWords) {
        FusionDataCollection hotelData = new FusionDataCollection();
        hotelData.setHotelCollection(iniHotelCollection());
        hotelData.setReviewCollection(iniReviewCollection());
        hotelData.setInvertedIndex(iniInvertedIndex(hotelData.reviewCollection, stopWords));
        return hotelData;
    }
}
