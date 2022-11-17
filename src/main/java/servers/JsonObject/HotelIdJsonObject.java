package servers.JsonObject;

/**
 * json object. Include two basic properties: success, hotelId
 */
public class HotelIdJsonObject {
    private Boolean success;
    private String hotelId;

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public HotelIdJsonObject(Boolean success, String hotelId) {
        this.success = success;
        this.hotelId = hotelId;
    }
}
