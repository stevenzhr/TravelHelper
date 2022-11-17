package servers.httpServer.HttpResponse;

public class JsonHttpResponse extends HttpResponse{
    /**
     * General constructor of a HttpResponse
     * @param statusCode  status code for the response
     */
    public JsonHttpResponse(int statusCode) {
        super(statusCode, "application/json; charset=UTF-8");
    }
}
