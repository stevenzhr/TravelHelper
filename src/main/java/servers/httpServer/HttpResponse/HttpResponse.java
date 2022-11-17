package servers.httpServer.HttpResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private Map<Integer, String> statusCodeMap;
    private final String responseLine;
    private final String header;
    private String body;

    /**
     * General constructor of a HttpResponse
     * @param statusCode status code for the response
     * @param contentType content type of the response
     */
    public HttpResponse(int statusCode, String contentType) {
        iniStatusMap();
        // first line of HTTP response
        responseLine = HTTP_VERSION + " " + statusCode + " " + statusCodeMap.get(statusCode);
        // Response header
        header = "Content-Type: " + contentType + System.lineSeparator() +
                "Date: " + LocalDateTime.now();
    }

    public HttpResponse(int statusCode) {
        iniStatusMap();
        // first line of HTTP response
        responseLine = HTTP_VERSION + " " + statusCode + " " + statusCodeMap.get(statusCode);
        // Response header
        header = "Content-Type: " + "text/html" + System.lineSeparator() +
                "Date: " + LocalDateTime.now();
    }

    /**
     * Set response body content
     * @param body content string in response content
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Initial status code map
     */
    private void iniStatusMap() {
        statusCodeMap = new HashMap<>();
        statusCodeMap.put(200, "OK");
        statusCodeMap.put(400, "Bad Request");
        statusCodeMap.put(403, "Forbidden");
        statusCodeMap.put(404, "Not Found");
        statusCodeMap.put(405, "Method Not Allowed");
        statusCodeMap.put(500, "Internal Server Error");
    }

    @Override
    public String toString() {
        return responseLine +
                System.lineSeparator() +
                header +
                System.lineSeparator() +
                System.lineSeparator() +
                body;
    }
}
