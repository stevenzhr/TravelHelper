package servers.httpServer.Handler;

import servers.httpServer.HttpRequest;

import java.io.PrintWriter;

/** Contains a method to process a http request from the client.  */
public interface HttpHandler {

    /**
     * Handles a http get request from the client
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
    void processRequest(HttpRequest request, PrintWriter writer);

    void setAttribute(Object data);
}
