package servers.jettyServer.Servlet;

import com.google.gson.Gson;
import hotelapp.hotelDataCollection.FusionDataCollection;
import hotelapp.hotelDataCollection.Review;
import hotelapp.hotelDataCollection.ThreadSafeReviewCollection;
import org.apache.commons.text.StringEscapeUtils;
import servers.JsonObject.HotelIdJsonObject;
import servers.JsonObject.ReviewJsonObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * HttpServlet for process /review endpoint request.
 */
public class ReviewsServlet extends HttpServlet {
    ThreadSafeReviewCollection tsReviewCollection;

    /**
     * Get method processor.
     * @param request client's http request
     * @param response Http response to client
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set attribute
        FusionDataCollection hotelData = (FusionDataCollection)getServletContext().getAttribute("hotelData");
        this.tsReviewCollection = (ThreadSafeReviewCollection)hotelData.reviewCollection;

        response.setContentType("application/json; charset=UTF-8");
        Gson json = new Gson();
        String jsonStr;

        // search QueryString for hotelId and num
        String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter("hotelId"));
        int num;
        try {
            num = Integer.parseInt(request.getParameter("num"));
        } catch (NumberFormatException e) {
            num = -1;
        }
        if(hotelId != null && num > 0) {
            Iterator<Review> it = tsReviewCollection.hotelReviewsIterator(hotelId);
            if (it != null) {
                // has hotelId key, have corresponded review data
                jsonStr = json.toJson(new ReviewJsonObject(true, hotelId, num, it));
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                // has hotelId key, no correspond review data, create jsonObject, hotelId="invalid"
                jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            // no hotelId key or num invalid, create jsonObject, hotelId="invalid"
            jsonStr = json.toJson(new HotelIdJsonObject(false, "invalid"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        PrintWriter out = response.getWriter();
        out.println(jsonStr);
    }
}
