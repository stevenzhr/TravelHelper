package hotelapp;

import hotelapp.hotelDataCollection.*;

import java.util.Iterator;
import java.util.Scanner;

public class QueryProcessor {
    private final String LINE_SEPARATOR = "";
    private final HotelCollection hotels;
    private final ReviewCollection reviews;
    private final InvertedIndex invertedIndex;

    private final boolean isParseReview;
    public QueryProcessor(HotelCollection hotels, ReviewCollection reviews, InvertedIndex invertedIndex) {
        this.hotels = hotels;
        this.reviews = reviews;
        this.invertedIndex = invertedIndex;
        this.isParseReview = reviews != null || invertedIndex != null;
    }

    public void processQueries() {
        // Start user interface
        printGuides();
        Scanner sc = new Scanner(System.in);
        String query = sc.nextLine();
        while (!query.equals("q")) {
            String[] queries = query.split(" ");
            if (queries.length == 1) {
                System.out.println("query need parameter, please input again. ");
                System.out.println(LINE_SEPARATOR);
                printGuides();
                query = sc.nextLine();
                continue;
            }
            switch(queries[0]) {
                case "find":
                    findHotel(queries[1], hotels);
                    break;
                case "findReviews":
                    findReviews(queries[1], reviews);
                    break;
                case "findWord":
                    findWord(queries[1], invertedIndex);
                    break;
                default:
                    System.out.println("Invalid command, please input again. ");
                    System.out.println(LINE_SEPARATOR);
                    printGuides();
            }
            query = sc.nextLine();
        }
        System.out.println("app exits");
        sc.close();
    }

    /**
     * Guides printer
     */
    private void printGuides() {
        System.out.println("Query guides:");
        System.out.println("find <hotelId> : show the general information about the hotel");
        System.out.println("findReviews <hotelId> : show all reviews for the hotel>");
        System.out.println("findWord <word> : show all reviews contain given word");
        System.out.println("q : exit the app");
        System.out.println();
        System.out.println("Please input a query: ");
    }

    /**
     * Hotel general information display method
     * @param hotelId hotel ID
     * @param hotels hotel list
     */
    private void findHotel (String hotelId, HotelCollection hotels) {
        Hotel hotel = hotels.getHotel(hotelId);
        if (hotel == null) {
            System.out.println("No hotel has been find. ");
            return;
        }
        System.out.println(hotel);
        System.out.println(LINE_SEPARATOR);
    }

    /**
     * Hotel reviews display method
     * @param hotelId hotel ID
     * @param reviews reviews list
     */
    private void findReviews (String hotelId, ReviewCollection reviews) {
        if (!isParseReview) {
            System.out.println("Can not run findReviews with out argument '-reviews'");
            return;
        }
        Iterator<Review> it = reviews.hotelReviewsIterator(hotelId);
        if (it == null) {
            System.out.println("Can't find any review related with hotel " + hotelId + ". Please try another one. ");
            return;
        }
        // result counter
        int count = 0;
        System.out.println(LINE_SEPARATOR);
        while (it.hasNext()) {
            System.out.println(it.next());
            System.out.println(LINE_SEPARATOR);
            count++;
        }
        System.out.println("There are total " + count + " reviews has been found. ");
    }

    private void findWord (String word, InvertedIndex invertedIndex) {
        if (!isParseReview) {
            System.out.println("Can not run findWord with out argument '-reviews'");
            return;
        }
        Iterator<Integer> it = invertedIndex.wordFreqIterator(word);
        if (it != null) {
            for (; it.hasNext(); ) {
                int i = it.next();
                System.out.println(">" + word + "< has shown " + i + " time in the following reviews:");
                for (Iterator<Review> ir = invertedIndex.wordFreqRevIterator(word, i); ir.hasNext(); ) {
                    System.out.println(ir.next());
                }
                System.out.println(LINE_SEPARATOR);
            }
        } else
            System.out.println("There is no review contains: " + word);


    }
}
