package servers.httpServer;

import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/** A class that represents a http request */
public class HttpRequest {
    // FILL IN CODE
    // Store parameters of the request
    private final InputStream input;
    private String type;
    private String path;
    private String version;
    private String host;
    private String connection;
    private Map<String, String> queryStringMap;

    /**
     * Constructor of class, need a socket inputStream as input
     * @param input socket InputStream
     */
    public HttpRequest(InputStream input) {
        this.input = input;
    }


    /**
     * Grab request string content form inputStream. Firstly create a sized StringBuffer and a sized byte array,
     * then fill the byte array by input.read(). Use the array to complete StringBuffer.
     * Finally, replace all "\r" and separate by "\n".
     */
    public void parse() {
        StringBuffer request = new StringBuffer(2048);
        byte[] buffer = new byte[2048];
        int byteNumber;
        try {
            byteNumber = input.read(buffer);
        }catch (IOException e) {
            e.printStackTrace();
            byteNumber = -1;
        }
        for (int i = 0; i < byteNumber; i++) {
            request.append((char)buffer[i]);
        }
        String stringContent = request.toString().replace("\r","");
        parseContent(stringContent.split("\\n"));
    }

    /**
     * Parse the content string array in parse() method.
     * @param requestList request content string array
     */
    private void parseContent(String[] requestList) {
        // check empty request
        if(requestList[0].equals(""))
            return;

        // check invalid format request
        if(requestList[0].split(" ").length != 3)
            return;

        this.type = requestList[0].split(" ")[0];
        this.version = requestList[0].split(" ")[2];

        for (String line : requestList) {
            if (line.startsWith("Host: "))
                this.host = line.split(" ")[1];
            if (line.startsWith("Connection: "))
                this.connection = line.split(" ")[1];
        }
        //Add detection for invalid path url. and detect arguments.
        String[] requestResource = requestList[0].split(" ")[1].split("\\?");
        this.path = requestResource[0];
        if (requestResource.length > 1) {
            // CLEAN THE STRING and split by "&"
            String[] queryStringList = StringEscapeUtils.escapeHtml4(requestResource[1]).split("&amp;");
            // Add queryString and value to map
            this.queryStringMap = new HashMap<>();
            for (String str : queryStringList) {
                if(str.split("=").length == 2) {
                    queryStringMap.put(str.split("=")[0],str.split("=")[1]);
                }
            }
        }

    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getConnection() {
        return connection;
    }

    public Map<String, String> getQueryStringMap() {
        return queryStringMap;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "input=" + input +
                ", type='" + type + '\'' +
                ", path='" + path + '\'' +
                ", version='" + version + '\'' +
                ", host='" + host + '\'' +
                ", connection='" + connection + '\'' +
                ", paramMap=" + queryStringMap +
                '}';
    }
}
