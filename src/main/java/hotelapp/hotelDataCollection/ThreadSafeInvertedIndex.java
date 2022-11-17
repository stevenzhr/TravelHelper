package hotelapp.hotelDataCollection;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeInvertedIndex extends InvertedIndex{

    private final ReentrantReadWriteLock lock;
    /**
     * Class constructor. Instantiate an object of wordSearchMap.
     *
     * @param filePath stop words list file path.
     */
    public ThreadSafeInvertedIndex(String filePath) {
        super(filePath);
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * A thread safe method of:
     * Return all reviews that contain input word. The review in return arraylist is sorted by word frequency and date.
     * @param word input word
     * @return an array list of all reviews contain input word
     */
    @Override
    public List<Review> search(String word) {
        try {
            lock.readLock().lock();
            return super.search(word);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * A thread safe method of:
     * Return first num of reviews base on the input which contain input word.
     * @param word input word
     * @param num number of reviews require
     * @return an array list of n reviews contain input word
     */
    public List<Review> search(String word, int num) {
        try {
            lock.readLock().lock();
            if (search(word) != null)
                return search(word).subList(0,num);
            else
                return null;
        } finally {
            lock.readLock().unlock();
        }
    }
}
