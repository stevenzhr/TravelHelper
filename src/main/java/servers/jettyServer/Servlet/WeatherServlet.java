package servers.jettyServer.Servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotelapp.hotelDataCollection.FusionDataCollection;
import hotelapp.hotelDataCollection.Hotel;
import hotelapp.hotelDataCollection.ThreadSafeHotelCollection;
import org.apache.commons.text.StringEscapeUtils;
import servers.JsonObject.HotelIdJsonObject;
import servers.JsonObject.HotelJsonObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HttpServlet for process /weather endpoint request.
 */
public class WeatherServlet extends HttpServlet {

    /**
     * Get method processor.
     * @param request client's http request
     * @param response Http response to client
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set attribute
        FusionDataCollection hotelData = (FusionDataCollection)getServletContext().getAttribute("hotelData");
        ThreadSafeHotelCollection tsHotelCollection = (ThreadSafeHotelCollection) hotelData.hotelCollection;

        response.setContentType("application/json; charset=UTF-8");
        Gson json = new Gson();
        String jsonStr;

        // search QueryString for hotelId
        String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter("hotelId"));
        String[] coordinates;

        if (hotelId != null) {
            Hotel hotel = tsHotelCollection.getHotel(hotelId);
            if (hotel != null) {
                // has hotelId key, have correspond hotel data
                jsonStr = json.toJson(new HotelJsonObject(true, hotelId, hotel));
                coordinates = hotel.getCoordinates();
                // get weather info by API through http request
                JsonObject weatherInfo = getWeatherInfo(coordinates);
                // Add weatherInfo to exist hotel json.
                JsonObject hotelJson= new JsonParser().parse(jsonStr).getAsJsonObject();
                hotelJson.add("current_weather", weatherInfo);
                jsonStr = hotelJson.toString();
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                // has hotelId key, no correspond hotel data, create jsonObject, hotelId="invalid"
                jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            // no hotelId key, create jsonObject, hotelId="invalid"
            jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        PrintWriter out = response.getWriter();
        out.println(jsonStr);
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
        return "GET " + pathAndQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator()
                + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
    }
}
