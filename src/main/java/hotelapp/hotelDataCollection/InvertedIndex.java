package hotelapp.hotelDataCollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InvertedIndex {
    // Properties
    private final Map<String, TreeMap<Integer, TreeSet<Review>>> wordSearchMap; // key -> word; value -> wordFrequencyMap
    private final SortedSet<String> stopWords;

    private Logger logger = LogManager.getLogger();

    /**
     * Class constructor. Instantiate an object of wordSearchMap.
     * @param filePath stop words list file path.
     */
    public InvertedIndex(String filePath) {
        wordSearchMap = new HashMap<>();
        stopWords = loadStopWords(filePath);

    }

    /**
     * Main process method of word frequency.
     * @param reviews input review list
     */
    public void processWord(ReviewCollection reviews) {
        logger.debug("Start process word frequency map");
        // Create an iterator for all kinds of hotelId in main review list.
        for (Iterator<String> hotelIdIterator = reviews.hotelIdIterator(); hotelIdIterator.hasNext(); ) {
            String hotelId = hotelIdIterator.next();
            // Create an iterator for all reviews belongs to the current hotelId.
            for (Iterator<Review> reviewIterator = reviews.hotelReviewsIterator(hotelId); reviewIterator.hasNext(); ) {
                Review review = reviewIterator.next();
                // Call word frequency calculation method and update the main word search map.
                calculateWordFreq(review);
            }
        }
        logger.debug("Finish process word frequency map");
    }

    private void calculateWordFreq(Review review) {
        List<String> words = wordPreprocess(review);
        // Calculate each word's frequency map
        Map<String, Integer> wordFreq = new HashMap<>();
        for (String word : words) {
            // check if in stop words
            if (!stopWords.contains(word)) {
                if (!wordFreq.containsKey(word))
                    wordFreq.put(word, 1);
                else
                    wordFreq.put(word, wordFreq.get(word) + 1);
            }
        }
        buildWordSearchMap(review, wordFreq);
    }

    /**
     * Preprocess the input review text. Firstly convert to lowercase and remove punctuations,
     * Then split the sentences by space and return the list of words
     * @param review target review object
     * @return list of words
     */
    private List<String> wordPreprocess(Review review) {
        // Get review text and convert all words to lowercase
        String content = review.getReviewText().toLowerCase(Locale.ENGLISH);
        // Remove all punctuation
        content = content.replaceAll("[^A-Za-z]", " ");
        // Split content by space
        return new ArrayList<>(Arrays.asList(content.split("\\s+")));
    }

    /**
     * Merge review's word freq map to wordSearchMap
     * @param review current review object
     * @param wordFreq review's word freq map
     */
    private void buildWordSearchMap(Review review, Map<String, Integer> wordFreq) {
        for (String word : wordFreq.keySet()) {
            // situation of wordSearchMap doesn't have the word key
            if (!wordSearchMap.containsKey(word))
                wordSearchMap.put(word, new TreeMap<>(Comparator.reverseOrder()));
            // situation of wordSearchMap returned TreeMap doesn't have word frequency value
            if (!wordSearchMap.get(word).containsKey(wordFreq.get(word)))
                wordSearchMap.get(word).put(wordFreq.get(word),new TreeSet<>());
            // put review to the TreeSet of reviews container
            wordSearchMap.get(word).get(wordFreq.get(word)).add(review);
        }
    }

    /**
     * Import stop words list.
     * @param filePath stop words list filepath.
     * @return list of stop words.
     */
    private SortedSet<String> loadStopWords(String filePath) {
        SortedSet<String> stopWords = new TreeSet<>();
        try (FileReader fr = new FileReader(filePath)) {
            BufferedReader bf = new BufferedReader(fr);
            String str = bf.readLine();
            while (str != null) {
                stopWords.add(str);
                str = bf.readLine();
            }
            return stopWords;
        } catch (IOException e) {
            System.out.println("Can't open stop words list file. " + e);
            return null;
        }
    }

    /**
     * Return all reviews that contain input word. The review in return arraylist is sorted by word frequency and date.
     * @param word input word
     * @return an array list of all reviews contain input word.
     */
    public List<Review> search(String word) {
        if (wordSearchMap.containsKey(word)) {
            // Get the tree map by word
            TreeMap<Integer, TreeSet<Review>> wordFreqMap = wordSearchMap.get(word);
            if (wordFreqMap == null)
                return null;
            // create a new array list to store return reviews
            ArrayList<Review> reviews = new ArrayList<>();
            // Iterate all the number of frequency in wordSearchMap
            for (Integer i : wordFreqMap.keySet()) {
                reviews.addAll(wordFreqMap.get(i));
            }
            return reviews;
        } else
            return null;

    }

    public Iterator<Integer> wordFreqIterator(String word) {
        if (wordSearchMap.containsKey(word))
            return wordSearchMap.get(word).keySet().iterator();
        else
            return null;
    }

    public Iterator<Review> wordFreqRevIterator(String word, int freq) {
        return wordSearchMap.get(word).get(freq).iterator();
    }
}
