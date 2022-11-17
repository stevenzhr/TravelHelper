package hotelapp.hotelDataCollection;

import hotelapp.MultiThreadJsonFileParser;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeReviewCollection extends ReviewCollection {
    private final ReentrantReadWriteLock lock;
    private final Map<String, TreeSet<Review>> reviewCollection;

    /**
     * Constructor, create a thread safe review collection
     * @param reviewsDirectory review's json file directory
     */
    public ThreadSafeReviewCollection (String reviewsDirectory, int nThreads) {
        super(reviewsDirectory);
        MultiThreadJsonFileParser multiThreadJsonFileParser = new MultiThreadJsonFileParser(reviewsDirectory, nThreads);
        this.reviewCollection = multiThreadJsonFileParser.traverseDirectory(reviewsDirectory);
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Inner class of thread safe hotelId iterator
     */
    private class HotelIdIterator implements Iterator<String> {
        Iterator<String> hotelIdIterator;

        public HotelIdIterator() {
            try {
                lock.readLock().lock();
                this.hotelIdIterator = reviewCollection.keySet().iterator();
            } finally {
                lock.readLock().unlock();
            }
        }

        public boolean hasNext() {
            try {
                lock.readLock().lock();
                return hotelIdIterator.hasNext();
            } finally {
                lock.readLock().unlock();
            }
        }

        public String next() {
            try {
                lock.readLock().lock();
                return  hotelIdIterator.next();
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    /**
     * Rerun a thread safe hotelId iterator
     * @return iterator of hotel ID
     */
    @Override
    public Iterator<String> hotelIdIterator() {
        return new HotelIdIterator();
    }

    /**
     * Inner class of thread safe hotel review iterator
     */
    private class HotelReviewsIterator implements Iterator<Review> {
        Iterator<Review> hotelReviewsIterator;

        public HotelReviewsIterator(String hotelId) {
            try {
                lock.readLock().lock();
                this.hotelReviewsIterator = reviewCollection.get(hotelId).iterator();
            } finally {
                lock.readLock().unlock();
            }
        }

        public boolean hasNext() {
            try {
                lock.readLock().lock();
                return hotelReviewsIterator.hasNext();
            } finally {
                lock.readLock().unlock();
            }
        }

        public Review next() {
            try {
                lock.readLock().lock();
                return  hotelReviewsIterator.next();
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    /**
     * Rerun a thread safe hotel review iterator
     * @param hotelId given hotel ID
     * @return iterator of reviews
     */
    @Override
    public Iterator<Review> hotelReviewsIterator(String hotelId) {
        try {
            lock.readLock().lock();
            if (reviewCollection.get(hotelId) == null)
                return null;
            else
                return new HotelReviewsIterator(hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }
}
