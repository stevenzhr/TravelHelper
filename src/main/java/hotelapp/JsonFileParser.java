package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotelapp.hotelDataCollection.Hotel;
import hotelapp.hotelDataCollection.Review;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class JsonFileParser {
    private String jsonFilePath;
    private Map<String, TreeSet<Review>> reviewsMap;  // String -> hotelId, TreeSet -> reviews by date

    public JsonFileParser() {
        reviewsMap = new HashMap<>();
        jsonFilePath = "";
    }
    public JsonFileParser(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    /**
     * Import hotels information from JSON file by input parameter.
     * And return hotel collection HashMap.
     * @return hotel collection HashMap
     */
    public Map<String, Hotel> parseHotelFile() {
        Map<String, Hotel> hotelsMap = new TreeMap<>();
        // Create a FileReader to read Json file
        try (FileReader fr = new FileReader(jsonFilePath)) {
            JsonParser parser = new JsonParser();
            JsonObject jo = parser.parse(fr).getAsJsonObject();
            // grub second layer's "sr" member and return as a json array
            JsonArray ja = jo.getAsJsonArray("sr");
            for (JsonElement element : ja) {
                JsonObject hE = element.getAsJsonObject();
                // Get hotel name and ID
                String hotelName = hE.get("f").getAsString();
                String hotelId = hE.get("id").getAsString();
                // Get hotel latitude and longitude
                String lat = hE.get("ll").getAsJsonObject().get("lat").getAsString();
                String lng = hE.get("ll").getAsJsonObject().get("lng").getAsString();
                // Get hotel Address (street + city),"Viale Lombardia 55, Milan"
                String ad = hE.get("ad").getAsString();
                String ci = hE.get("ci").getAsString() + ", " + hE.get("pr").getAsString();
                Hotel hotel = new Hotel(hotelName, hotelId, lat, lng, ad, ci);
                hotelsMap.put(hotelId, hotel);
            }
        }catch(IOException e) {
            System.out.println("Could not read this file: " + e);
        }
        return hotelsMap;
    }

    /**
     * Parse reviews from one single Json file and add to reviewMap.
     * @param file json file path
     */
    public void parseReviewFile(File file) {
        // create file reader and start parse Json file.
        try (FileReader fr = new FileReader(file)) {
            // Create json parser
            JsonParser parser = new JsonParser();
            // Read whole json file as a jsonObject
            JsonObject jo = parser.parse(fr).getAsJsonObject();
            // Get review Json array
            JsonArray ja = jo.get("reviewDetails").getAsJsonObject()
                    .get("reviewCollection").getAsJsonObject()
                    .get("review").getAsJsonArray();
            for (JsonElement elem : ja) {
                // convert JsonElement to JsonObject
                JsonObject rObj = elem.getAsJsonObject();
                // Create a review builder
                Review.Builder builder = new Review.Builder(
                        rObj.get("reviewId").getAsString(),
                        rObj.get("reviewText").getAsString());
                // Complete review's other information
                Review review = builder.hotelId(rObj.get("hotelId").getAsString())
                        .ratingOverall(rObj.get("ratingOverall").getAsInt())
                        .title(rObj.get("title").getAsString())
                        .userNickname(rObj.get("userNickname").getAsString())
                        .submissionTime(rObj.get("reviewSubmissionTime").getAsString()).build();
                // Add review to HashMap reviews
                // Check HashMap exist review's hotelId
                if (!reviewsMap.containsKey(review.getHotelId()))
                    reviewsMap.put(review.getHotelId(), new TreeSet<>());
                reviewsMap.get(review.getHotelId()).add(review);
            }
        }catch (IOException e) {
            System.out.println("Can not read this file. " + e);
        }
    }

    public Map<String, TreeSet<Review>> traverseDirectory(String reviewsDirectory) {
        // Recursively traverse the input directory to find all json file and call parseJsonFile method.
        File file = new File(reviewsDirectory);
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            if (f.isDirectory())
                traverseDirectory(f.getPath());
            else if (f.getName().endsWith(".json"))
                parseReviewFile(f);
        }
        return reviewsMap;
    }
}
