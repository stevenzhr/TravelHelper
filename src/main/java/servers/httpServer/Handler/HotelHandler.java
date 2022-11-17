package servers.httpServer.Handler;

import com.google.gson.Gson;
import hotelapp.hotelDataCollection.FusionDataCollection;
import hotelapp.hotelDataCollection.Hotel;
import hotelapp.hotelDataCollection.ThreadSafeHotelCollection;
import servers.JsonObject.HotelJsonObject;
import servers.JsonObject.HotelIdJsonObject;
import servers.httpServer.HttpRequest;
import servers.httpServer.HttpResponse.HttpResponse;
import servers.httpServer.HttpResponse.JsonHttpResponse;

import java.io.PrintWriter;

/**
 * HttpHandler for process /hotelInfo endpoint request.
 */
public class HotelHandler implements HttpHandler{
    private ThreadSafeHotelCollection tsHotelCollection;

    /**
     * Constructor
     * @param hotelFusionData thread-safe hotel data collection
     */
    public HotelHandler(FusionDataCollection hotelFusionData) {
        this.tsHotelCollection = (ThreadSafeHotelCollection)hotelFusionData.hotelCollection;
    }

    /**
     * Base on request type switch to relevant request type method
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {
        String requestType = request.getType();
        switch (requestType) {
            case "GET":
                doGet(request, writer);
                break;
            default:
                writer.println(new HttpResponse(405));
                break;
        }
    }

    @Override
    public void setAttribute(Object data) {
        try {
            tsHotelCollection = (ThreadSafeHotelCollection) data;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get method processor.
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
    private void doGet(HttpRequest request, PrintWriter writer) {
        JsonHttpResponse response;
        Gson json = new Gson();
        String jsonStr;

        String hotelId = null;

        // search QueryString for hotelId
        try {
            hotelId = request.getQueryStringMap().get("hotelId");
        } catch (NullPointerException e) {
            System.out.println("QueryString invalid");
        }
        if (hotelId == null) {
            // no hotelId key, create jsonObject, hotelId="invalid"
            jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
            response = new JsonHttpResponse(400);
        } else {
            Hotel hotel = tsHotelCollection.getHotel(hotelId);
            if (hotel == null) {
                // has hotelId key, no correspond hotel data, create jsonObject, hotelId="invalid"
                jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
                response = new JsonHttpResponse(400);
            } else {
                // has hotelId key, have correspond hotel data
                jsonStr = json.toJson(new HotelJsonObject(true, hotelId, hotel));
                response = new JsonHttpResponse(200);
            }
        }
        response.setBody(jsonStr);
        writer.println(response);
    }
}
