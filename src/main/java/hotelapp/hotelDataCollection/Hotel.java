package hotelapp.hotelDataCollection;


//public class Hotel implements Iterable<Review>{
public class Hotel{
    // Constants
    private final int LATITUDE = 0;
    private final int LONGITUDE = 1;
    // Properties
    private final String hotelName;
    private final String hotelId;
    private final String[] coordinates = new String[2];
    private final String address;
    private final String city;


    /**
     * Class Hotel's constructor. All arguments must be String.
     * @param hotelName hotel name, "San Francisco Hotel"
     * @param hotelId hotel ID, "120"
     * @param latitude hotel latitude, "45.485520"
     * @param longitude hotel longitude, "9.223450"
     * @param address hotel street address "Viale Lombardia 55"
     * @param city hotel city (city + state) ", Milan, MI"
     */
    public Hotel (String hotelName, String hotelId, String latitude, String longitude, String address, String city){
        this.hotelName = hotelName;
        this.hotelId = hotelId;
        this.coordinates[LATITUDE] = latitude;
        this.coordinates[LONGITUDE] = longitude;
        this.address = address;
        this.city = city;
    }

    public Hotel (Hotel hotel) {
        this.hotelName = hotel.hotelName;
        this.hotelId = hotel.hotelId;
        this.coordinates[LATITUDE] = hotel.coordinates[LATITUDE];
        this.coordinates[LONGITUDE] = hotel.coordinates[LONGITUDE];
        this.address = hotel.address;
        this.city = hotel.city;
    }

    /**
     * Getter for hotelId
     * @return hotel ID
     */
    public String getHotelId() { return this.hotelId; }

    public String getHotelName() { return this.hotelName; }

    public String getAddress() { return this.address; }

    public String getCity() { return this.city; }

    public String[] getCoordinates() { return this.coordinates; }

    @Override
    public String toString() {
        return System.lineSeparator() +
                "********************" + System.lineSeparator() +
                hotelName + ": " + hotelId + System.lineSeparator() +
                address + System.lineSeparator() +
                city + System.lineSeparator();
    }
}
