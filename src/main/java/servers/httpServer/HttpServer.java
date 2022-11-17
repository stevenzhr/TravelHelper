package servers.httpServer;

import hotelapp.hotelDataCollection.FusionDataCollection;
import servers.httpServer.Handler.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Implements a http server using raw sockets */
public class HttpServer{
    private final Map<String, String> handlers; // maps each url path to the appropriate handler
    private final int port;
    private boolean alive;
    final private Object hotelFusionData;

    public HttpServer(int port, Object hotelFusionData) {
        this.port = port;
        this.hotelFusionData = hotelFusionData;
        alive = true;
        handlers = new HashMap<>();
    }

    /**
     * Add a map of path and handler to the handlers.
     * @param path string of path
     * @param handler name of handler
     */
    public void addHandlerMap(String path, String handler) {
        handlers.put(path, handler);
    }


    /**
     * Simple server start method. Create thread pool -> set welcomingSocket -> submit worker
     */
    public void startServer() {
        // create thread pool
        final ExecutorService threads = Executors.newCachedThreadPool();
        Runnable serverTask = () -> {
            try {
                // create welcoming socket
                ServerSocket welcomingSocket = new ServerSocket(port);
                System.out.println("Server: Waiting for connection... ");
                while (alive) {
                    Socket clientSocket = welcomingSocket.accept();
                    threads.submit(new ClientTask(clientSocket));
                }
                if (!alive) {
                    welcomingSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Exception occurred while using the socket: " + e);
                e.printStackTrace();
            }
        };
        // create server thread and start
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    /**
     * Inner class for socket thread worker
     */
    private class ClientTask implements Runnable {
        private InputStream input;
        private PrintWriter writer;
        private Socket clientSocket;

        /**
         * Constructor of socket thread worker. Set input and output. Throw out any exception.
         * @param clientSocket connected socket
         */
        public ClientTask(Socket clientSocket) {
            try {
                this.clientSocket = clientSocket;
                this.input = clientSocket.getInputStream();
                this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                System.out.println("Exception occurred while using the socket: " + e);
                e.printStackTrace();
            }
        }

        /**
         * Works in each socket thread.
         */
        @Override
        public void run() {
            System.out.println("Connection has been established. ");
            HttpRequest request = new HttpRequest(input);
            request.parse();
            if (request.getType() != null) {
                String handler = request.getPath();
                System.out.println("Call path: " + handler);
                try {
                    // check unimplemented method
                    if (handler.equals("/"))
                        new ErrorHandler(405).processRequest(request, writer);
                    // check unknown url
                    else if (!handlers.containsKey(handler))
                        new ErrorHandler(404).processRequest(request, writer);
                    // all clear, create handler instance
                    else {
                        HttpHandler httpHandler = (HttpHandler) Class.forName("servers.httpServer.Handler." + handlers.get(handler)).
                                getConstructor(FusionDataCollection.class).newInstance(hotelFusionData);
                        httpHandler.processRequest(request, writer);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            // Finish process request and close socket
            try {
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
