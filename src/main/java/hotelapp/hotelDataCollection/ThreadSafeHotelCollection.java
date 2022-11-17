package hotelapp.hotelDataCollection;

import hotelapp.MultiThreadJsonFileParser;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeHotelCollection extends HotelCollection {
    private final ReentrantReadWriteLock lock;
    private final Map<String, Hotel> hotelCollection;

    /**
     * Constructor, create a thread safe hotel collection
     * @param filename input JSON file path and file name.
     */
    public ThreadSafeHotelCollection(String filename, int nThreads) {
        this.lock = new ReentrantReadWriteLock();
        MultiThreadJsonFileParser mParser = new MultiThreadJsonFileParser(filename, nThreads);
        hotelCollection = mParser.parseHotelFile();
    }

    /**
     * Thread safe getter for hotel collection
     * @param hotelId hotel ID
     * @return immutable hotel object
     */
    @Override
    public Hotel getHotel(String hotelId) {
        try {
            lock.readLock().lock();
            if (hotelCollection.get(hotelId) == null)
                return null;
            else
                return new Hotel(hotelCollection.get(hotelId));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Inner class of thread safe hotelId iterator
     */
    private class HotelIdIterator implements Iterator<String> {
        Iterator<String> hotelIdIterator;

        public HotelIdIterator() {
            try {
                lock.readLock().lock();
                this.hotelIdIterator = hotelCollection.keySet().iterator();
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
}
