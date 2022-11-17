package servers.httpServer.Handler;

import servers.httpServer.HttpRequest;
import servers.httpServer.HttpResponse.HttpResponse;

import java.io.PrintWriter;

/**
 * A httpHandler that process error request. Only return error status code
 */
public class ErrorHandler implements HttpHandler{
    private final int errorCode;

    /**
     * Constructor
     * @param errorCode error status code
     */
    public ErrorHandler (int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Return a response with error status code.
     * @param request client's http request
     * @param writer PrintWriter of the response
     */
    @Override
    public void processRequest(HttpRequest request, PrintWriter writer) {
        HttpResponse response = new HttpResponse(errorCode);
        writer.println(response);
    }

    @Override
    public void setAttribute(Object data) {
    }
}
