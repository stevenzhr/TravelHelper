package servers.jettyServer.Servlet;

import com.google.gson.Gson;
import hotelapp.hotelDataCollection.FusionDataCollection;
import hotelapp.hotelDataCollection.Review;
import hotelapp.hotelDataCollection.ThreadSafeInvertedIndex;
import org.apache.commons.text.StringEscapeUtils;
import servers.JsonObject.InvertIndexJsonObject;
import servers.JsonObject.WordJsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * HttpServlet for process /index endpoint request.
 */
public class WordServlet extends HttpServlet {
    private ThreadSafeInvertedIndex tsInvertIndex;

    /**
     * Get method processor.
     * @param request client's http request
     * @param response Http response to client
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set attribute
        FusionDataCollection hotelData = (FusionDataCollection)getServletContext().getAttribute("hotelData");
        this.tsInvertIndex = (ThreadSafeInvertedIndex)hotelData.invertedIndex;

        response.setContentType("application/json; charset=UTF-8");
        Gson json = new Gson();
        String jsonStr;

        // search QueryString for hotelId and num
        String word = StringEscapeUtils.escapeHtml4(request.getParameter("word"));
        int num;
        try {
            num = Integer.parseInt(request.getParameter("num"));
        } catch (NumberFormatException e) {
            num = -1;
        }
        if (word != null && num > 0) {
            // has word key, has corresponded hotel data
            List<Review> reviews = tsInvertIndex.search(word, num);
            if (reviews != null) {
                jsonStr = json.toJson(new InvertIndexJsonObject(true, word, reviews));
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                // has word key, no correspond review data, create jsonObject, hotelId="invalid"
                jsonStr = json.toJson(new WordJsonObject(false, "invalid"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            // no word key or num invalid, create jsonObject, hotelId="invalid"
            jsonStr = json.toJson(new WordJsonObject(false, "invalid"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        PrintWriter out = response.getWriter();
        out.println(jsonStr);

    }
}
