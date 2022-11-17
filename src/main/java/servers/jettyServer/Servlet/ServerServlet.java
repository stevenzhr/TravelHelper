package servers.jettyServer.Servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HttpServlet for process undefined endpoint request.
 */
public class ServerServlet extends HttpServlet {
    /**
     * Get method processor. Only reply response with error status code.
     * @param request client's http request
     * @param response Http response to client
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getServletPath());
        response.setStatus(405);
    }
}
