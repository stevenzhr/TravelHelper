package hotelapp.hotelDataCollection;

import hotelapp.JsonFileParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HotelCollection {
    // Properties
    private final Map<String, Hotel> hotelsMap;

    public HotelCollection() {
        hotelsMap = new HashMap<>();
    }
    /**
     * Constructor of class Hotels. Import hotels information from JSON file by input parameter.
     * And fill hotels HashMap.
     * @param filename input JSON file path and file name.
     */
    public HotelCollection(String filename) {
        JsonFileParser jsonFileParser = new JsonFileParser(filename);
        hotelsMap = jsonFileParser.parseHotelFile();
    }

    /**
     * Return the size of HashMap hotels as an integer.
     * @return size of hotels
     */
    public int size() {
        return hotelsMap.size();
    }

    /**
     * Get a hotel object by hotel ID.
     * @param hotelId hotel ID
     * @return hotel object
     */
    public Hotel getHotel(String hotelId) {
        if (hotelsMap.containsKey(hotelId))
            return new Hotel(hotelsMap.get(hotelId));
        else
            return null;
    }

    public Iterator<String> hotelIdIterator() {
        return hotelsMap.keySet().iterator();
    }
}
