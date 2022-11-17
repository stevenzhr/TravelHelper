package servers.JsonObject;

import hotelapp.hotelDataCollection.Hotel;

public class HotelJsonObject{

    private final Boolean success;
    private final String hotelId;
    private final String name;
    private final String addr;
    private final String city;
    private final String state;
    private final String lat;
    private final String lng;

    public HotelJsonObject(Boolean success, String hotelId, Hotel hotel) {
//        super(success, hotelId);
//        parse(hotel);
        this.success = success;
        this.hotelId = hotelId;
        this.name = hotel.getHotelName();
        this.addr = hotel.getAddress();
        String[] cityAndState = hotel.getCity().split(", ");
        this.city = cityAndState[0];
        if (cityAndState.length != 2)
            state = "n/a";
        else
            state = cityAndState[1];
        this.lat = hotel.getCoordinates()[0];
        this.lng = hotel.getCoordinates()[1];
    }

    /**
     * Parse hotel object data and fill in to corresponding properity.
     * @param hotel input Hotel object
     */
//    private void parse(Hotel hotel) {
//        this.name = hotel.getHotelName();
//        this.addr = hotel.getAddress();
//        String[] cityAndState = hotel.getCity().split(", ");
//        this.city = cityAndState[0];
//        if (cityAndState.length != 2)
//            state = "n/a";
//        else
//            state = cityAndState[1];
//        this.lat = hotel.getCoordinates()[0];
//        this.lng = hotel.getCoordinates()[1];
//    }


}
