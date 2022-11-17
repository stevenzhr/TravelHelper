package servers.httpServer.Handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotelapp.hotelDataCollection.FusionDataCollection;
import hotelapp.hotelDataCollection.Hotel;
import hotelapp.hotelDataCollection.ThreadSafeHotelCollection;
import servers.JsonObject.HotelIdJsonObject;
import servers.JsonObject.HotelJsonObject;
import servers.httpServer.HttpRequest;
import servers.httpServer.HttpResponse.HttpResponse;
import servers.httpServer.HttpResponse.JsonHttpResponse;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HttpHandler for process /weather endpoint request.
 */
public class WeatherHandler implements HttpHandler{

    private ThreadSafeHotelCollection tsHotelCollection;

    /**
     * Constructor
     * @param hotelFusionData thread-safe hotel data collection
     */
    public WeatherHandler(FusionDataCollection hotelFusionData) {
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
        String[] coordinates;

        // search QueryString for hotelId
        try {
            hotelId = request.getQueryStringMap().get("hotelId");
        } catch(NullPointerException e) {
            System.out.println("QueryString invalid");
        }

        if (hotelId == null) {
            // no hotelId key, create jsonObject, hotelId="invalid"
            jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
            response = new JsonHttpResponse(400);
        }
        else {
            Hotel hotel = tsHotelCollection.getHotel(hotelId);
            if (hotel == null) {
                // has hotelId key, no correspond hotel data, create jsonObject, hotelId="invalid"
                jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
                response = new JsonHttpResponse(400);
            }
            else {
                // has hotelId key, have correspond hotel data
                jsonStr = json.toJson(new HotelJsonObject(true, hotelId, hotel));
                coordinates = hotel.getCoordinates();
                // get weather info by API through http request
                JsonObject weatherInfo = getWeatherInfo(coordinates);
                // Add weatherInfo to exist hotel json.
                JsonObject hotelJson= new JsonParser().parse(jsonStr).getAsJsonObject();
                hotelJson.add("current_weather", weatherInfo);
                jsonStr = hotelJson.toString();
                response = new JsonHttpResponse(200);
            }
        }

        response.setBody(jsonStr);
        writer.println(response);
    }

    /**
     * Get weather info by API through http request
     * @param coordinates latitude and longitude string array
     * @return JsonObject of currentWeather
     */
    private JsonObject getWeatherInfo(String[] coordinates) {
        JsonObject jsonObject = null;
        String urlString = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=" + Double.parseDouble(coordinates[0]) +
                "&longitude=" + Double.parseDouble(coordinates[1]) +
                "&current_weather=true";
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // Parse urlString by URL class
            URL url = new URL(urlString);
            socket = new Socket(url.getHost(), 80);

            // Send http request to api
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            String request = getRequest(url.getHost(), url.getPath() + "?" + url.getQuery());
            out.println(request);

            // Receive response from api
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            // Call getJsonContent to draw json String form response content
            String temp = getJsonContent(sb.toString());
            if (temp != null)
                jsonObject = new JsonParser().parse(temp).getAsJsonObject().get("current_weather").getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close every thing
            try {
                if (socket != null)
                    socket.close();
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * A method Using regex to find a particular Json string pattern from api.open-meteo.com.
     * @param text Http response content text
     * @return pure json string
     */
    private String getJsonContent(String text) {
        Pattern regexPattern = Pattern.compile("(\\{.*}})");
        Matcher matcher = regexPattern.matcher(text);
        if (matcher.find())
            return matcher.group(1);
        else
            return null;
    }

    /**
     * Create a get request String
     * @param host target host
     * @param pathAndQuery path and queryString
     * @return HttpRequest
     */
    private static String getRequest(String host, String pathAndQuery) {
        String request = "GET " + pathAndQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator()
                + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
        return request;
    }

    @Override
    public void setAttribute(Object data) {
        try {
            tsHotelCollection = (ThreadSafeHotelCollection) data;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
