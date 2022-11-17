package servers.httpServer.Handler;

import com.google.gson.Gson;
import hotelapp.hotelDataCollection.*;
import servers.JsonObject.HotelIdJsonObject;
import servers.JsonObject.ReviewJsonObject;
import servers.httpServer.HttpRequest;
import servers.httpServer.HttpResponse.HttpResponse;
import servers.httpServer.HttpResponse.JsonHttpResponse;

import java.io.PrintWriter;
import java.util.Iterator;

/**
 * HttpHandler for process /reviews endpoint request.
 */
public class ReviewsHandler implements HttpHandler{
    ThreadSafeReviewCollection tsReviewCollection;

    /**
     * Constructor
     * @param hotelFusionData thread-safe hotel data collection
     */
    public ReviewsHandler(FusionDataCollection hotelFusionData) {
        this.tsReviewCollection = (ThreadSafeReviewCollection)hotelFusionData.reviewCollection;
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
            tsReviewCollection = (ThreadSafeReviewCollection) data;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get method processor
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
    private void doGet(HttpRequest request, PrintWriter writer) {
        JsonHttpResponse response;
        Gson json = new Gson();
        String jsonStr;

        String hotelId = null;
        int num = -1;

        // search QueryString for hotelId and num
        try {
            hotelId = request.getQueryStringMap().get("hotelId");
            num = Integer.parseInt(request.getQueryStringMap().get("num"));
        } catch(NullPointerException | NumberFormatException e) {
            System.out.println("QueryString invalid");
        }

        if (hotelId == null || num < 0) {
            // no hotelId key or num invalid, create jsonObject, hotelId="invalid"
            jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
            response = new JsonHttpResponse(400);
        }
        else {
            Iterator<Review> it = tsReviewCollection.hotelReviewsIterator(hotelId);
            if (it == null) {
                // has hotelId key, no correspond review data, create jsonObject, hotelId="invalid"
                jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
                response = new JsonHttpResponse(400);
            }
            else {
                // has hotelId key, have corresponded review data
                jsonStr = json.toJson(new ReviewJsonObject(true, hotelId, num, it));
                response = new JsonHttpResponse(200);
            }
        }
        response.setBody(jsonStr);
        writer.println(response);
    }
}
