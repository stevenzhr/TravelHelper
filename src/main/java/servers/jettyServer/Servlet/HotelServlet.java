package servers.jettyServer.Servlet;

import com.google.gson.Gson;
import hotelapp.hotelDataCollection.FusionDataCollection;
import hotelapp.hotelDataCollection.Hotel;
import hotelapp.hotelDataCollection.ThreadSafeHotelCollection;
import org.apache.commons.text.StringEscapeUtils;
import servers.JsonObject.HotelIdJsonObject;
import servers.JsonObject.HotelJsonObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * HttpServlet for process /hotelInfo endpoint request.
 */
public class HotelServlet extends HttpServlet {

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
        if (hotelId != null) {
            Hotel hotel = tsHotelCollection.getHotel(hotelId);
            if (hotel != null) {
                // has hotelId key, have correspond hotel data
                jsonStr = json.toJson(new HotelJsonObject(true, hotelId, hotel));
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else {
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
}
