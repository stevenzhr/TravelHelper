package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotelapp.hotelDataCollection.Hotel;
import hotelapp.hotelDataCollection.Review;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MultiThreadJsonFileParser extends JsonFileParser{
    private final ReentrantReadWriteLock lock;
    private ExecutorService poolThreads;
    private Phaser phaser;
    private Logger logger = LogManager.getLogger();
    private SortedMap<String, TreeSet<Review>> reviewCollection;  // String -> hotelId, TreeSet -> reviews by date

    public MultiThreadJsonFileParser(String jsonFilePath, int nThreads) {
        super(jsonFilePath);
        lock = new ReentrantReadWriteLock();
        poolThreads = Executors.newFixedThreadPool(nThreads);
        phaser = new Phaser();
        reviewCollection = new TreeMap<>();
    }

    /**
     * Thread safe import hotels information from JSON file by input parameter.
     * And return hotel collection HashMap.
     * @return hotel collection HashMap
     */
    @Override
    public Map<String, Hotel> parseHotelFile() {
        lock.writeLock().lock();
        try {
            return super.parseHotelFile();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private class Worker implements Runnable {
        private File file;
        private List<Review> reviewList; // thread local review collection

        public Worker (File file) {
            this.file = file;
            reviewList = new ArrayList<>();
        }

        /**
         * Parser the given json file and return a list of all reviews contain in the json file
         */
        @Override
        public void run() {
            parserJsonFile();
            mergeMaps(reviewList);
            phaser.arriveAndDeregister();
            logger.debug("A worker has done its job " + this);
        }
        private void parserJsonFile() {
            // create file reader and start parse Json file.
            try (FileReader fr = new FileReader(file)) {
                JsonParser parser = new JsonParser();
                JsonObject jo = parser.parse(fr).getAsJsonObject();
                // Get review Json array
                JsonArray ja = jo.get("reviewDetails").getAsJsonObject()
                        .get("reviewCollection").getAsJsonObject()
                        .get("review").getAsJsonArray();
                for (JsonElement elem : ja) {
                    // convert JsonElement to JsonObject
                    JsonObject rObj = elem.getAsJsonObject();
                    Review.Builder builder = new Review.Builder(
                            rObj.get("reviewId").getAsString(),
                            rObj.get("reviewText").getAsString());
                    // Complete review's other information
                    Review review = builder.hotelId(rObj.get("hotelId").getAsString())
                            .ratingOverall(rObj.get("ratingOverall").getAsInt())
                            .title(rObj.get("title").getAsString())
                            .userNickname(rObj.get("userNickname").getAsString())
                            .submissionTime(rObj.get("reviewSubmissionTime").getAsString()).build();
                    reviewList.add(review);
                }
            }catch (IOException e) {
                System.out.println("Can not read this file. " + e);
            }
        }
    }

    /**
     * Recursively traverse the input directory to find all json file and create a thread to parse it.
     * @param reviewsDirectory path of Json review file
     * @return A map of parsed reviews in all Json file
     */
    @Override
    public Map<String, TreeSet<Review>> traverseDirectory(String reviewsDirectory) {
        logger.debug("Start parse directory");
        try {
            traverseDirectoryHelper(reviewsDirectory);
        } finally {
            phaser.awaitAdvance(phaser.getPhase());
            shutdownPool();
        }
        logger.debug("Finish parse directory");
        return reviewCollection;
    }

    private void traverseDirectoryHelper (String reviewsDirectory) {
        File file = new File(reviewsDirectory);
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            if (f.isDirectory())
                traverseDirectoryHelper(f.getPath());
            else if (f.getName().endsWith(".json")) {
                Worker worker = new Worker(f);
                logger.debug("Created a worker for " + f.getPath());
                phaser.register();
                poolThreads.submit(worker);
            }
        }
    }

    /**
     * Thread pool shutdown procedure
     */
    public void shutdownPool() {
        poolThreads.shutdown();
        try {
            poolThreads.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread safe merge a ListArray of review to the review collection
     * @param reviewList input review ListArray
     */
    private void mergeMaps(List<Review> reviewList) {
        try {
            lock.writeLock().lock();
            for (Review review : reviewList) {
                if (!reviewCollection.containsKey(review.getHotelId()))
                    reviewCollection.put(review.getHotelId(), new TreeSet<>());
                reviewCollection.get(review.getHotelId()).add(review);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
