package servers.httpServer.Handler;

import com.google.gson.Gson;
import hotelapp.hotelDataCollection.FusionDataCollection;
import hotelapp.hotelDataCollection.Review;
import hotelapp.hotelDataCollection.ThreadSafeInvertedIndex;
import servers.JsonObject.InvertIndexJsonObject;
import servers.JsonObject.WordJsonObject;
import servers.httpServer.HttpRequest;
import servers.httpServer.HttpResponse.HttpResponse;
import servers.httpServer.HttpResponse.JsonHttpResponse;

import java.io.PrintWriter;
import java.util.List;

/**
 * HttpHandler for process /index endpoint request.
 */
public class WordHandler implements HttpHandler{
    private ThreadSafeInvertedIndex tsInvertIndex;

    /**
     * Constructor
     * @param hotelFusionData thread-safe hotel data collection
     */
    public WordHandler(FusionDataCollection hotelFusionData) {
        this.tsInvertIndex = (ThreadSafeInvertedIndex)hotelFusionData.invertedIndex;
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

    /**
     * Get method processor.
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
    private void doGet(HttpRequest request, PrintWriter writer) {
        JsonHttpResponse response;
        Gson json = new Gson();
        String jsonStr;

        String word = null;
        int num = -1;

        // search QueryString for word and num
        try {
            word = request.getQueryStringMap().get("word");
            num = Integer.parseInt(request.getQueryStringMap().get("num"));
        } catch (NullPointerException | NumberFormatException e) {
            System.out.println("QueryString invalid");
        }

        if (word == null || num < 0) {
            // no word key or num invalid, create jsonObject, hotelId="invalid"
            jsonStr = json.toJson(new WordJsonObject(false, "invalid"));
            response = new JsonHttpResponse(400);
        }
        else {
            List<Review> reviews = tsInvertIndex.search(word, num);
            if (reviews == null) {
                // has word key, no correspond review data, create jsonObject, hotelId="invalid"
                jsonStr = json.toJson(new WordJsonObject(false, "invalid"));
                response = new JsonHttpResponse(400);
            }
            else {
                // has word key, has corresponded hotel data
                jsonStr = json.toJson(new InvertIndexJsonObject(true, word, reviews));
                response = new JsonHttpResponse(200);
            }
        }

        response.setBody(jsonStr);
        writer.println(response);
    }

    @Override
    public void setAttribute(Object data) {
        try {
            tsInvertIndex = (ThreadSafeInvertedIndex) data;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
