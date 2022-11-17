package servers.jettyServer;

import hotelapp.hotelDataCollection.FusionDataCollection;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import servers.jettyServer.Servlet.*;

/** This class uses Jetty & servlets to implement server serving hotel and review info */
public class JettyServer {
    // FILL IN CODE
    private final int port;
    private boolean alive;
    final private Object hotelFusionData;

    public JettyServer(int port, Object hotelFusionData) {
        this.port = port;
        this.hotelFusionData = hotelFusionData;
        this.alive = true;
    }

    /**
     * Function that starts the server
     * @throws Exception throws exception if access failed
     */
    public  void start() throws Exception {
        Server server = new Server(port); // jetty server

        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setAttribute("hotelData", hotelFusionData);

        contextHandler.addServlet(HotelServlet.class, "/hotelInfo");
        contextHandler.addServlet(ReviewsServlet.class, "/reviews");
        contextHandler.addServlet(WordServlet.class, "/index");
        contextHandler.addServlet(WeatherServlet.class, "/weather");
        contextHandler.addServlet(ServerServlet.class, "");

        server.setHandler(contextHandler);

        server.start();
        server.join();
    }

}
