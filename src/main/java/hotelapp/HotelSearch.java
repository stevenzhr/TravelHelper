package hotelapp;

import hotelapp.Utilities.ArgumentParser;
import hotelapp.hotelDataCollection.HotelCollection;
import hotelapp.hotelDataCollection.FusionDataCollection;
import hotelapp.hotelDataCollection.InvertedIndex;
import hotelapp.hotelDataCollection.ReviewCollection;
import servers.httpServer.HttpServer;
import servers.jettyServer.JettyServer;

import java.util.HashMap;


/** The main class for project 1.
 * The main function should take the following 4 command line arguments:
 * -hotels hotelFile -reviews reviewsDirectory
 * and read general information about the hotels from the hotelFile (a JSON file)
 * and read hotel reviews from the json files in reviewsDirectory.
 * The data should be loaded into data structures that allow efficient search.
 * See the pdf description for details.
 * You are expected to add other classes and methods to this project.
 */
public class HotelSearch {
    private static final String[] REQUIRED_ARGUMENTS = {"-hotels"};
    private static final String STOP_WORD_LIST_FILEPATH = "input/StopWordsList.txt";
    private static final int PORT = 8080;


    public static void main(String[] args) throws Exception {
        // Call argument parser to get json file path
        ArgumentParser ap = new ArgumentParser(REQUIRED_ARGUMENTS);
        HashMap<String, String> commandLineArgsMap = ap.parseArguments(args);

        // app initialization
        AppInitializer initializer = new AppInitializer(commandLineArgsMap);

        // Instance hotel, review collection object and compute invert index
        HotelCollection hotels = initializer.iniHotelCollection();
        ReviewCollection reviews = initializer.iniReviewCollection();
        InvertedIndex invertedIndex = initializer.iniInvertedIndex(reviews, STOP_WORD_LIST_FILEPATH);

//        //ONLY FOR JAR FILE USE, HARD CODE FOR SPECIFIC SERVER
//        commandLineArgsMap.put("-server", "jetty");

        // Check args have "-server"
        if (commandLineArgsMap.containsKey("-server")) {
            // Create server and start the server
            FusionDataCollection fusionDataCollection = new FusionDataCollection(hotels, reviews, invertedIndex);
            startServer(commandLineArgsMap, fusionDataCollection);
        }

        // Check args have "-output"
        if (commandLineArgsMap.containsKey("-output")) {
            // Output reviews
            OutputPrinter printer = new OutputPrinter(hotels, reviews, commandLineArgsMap.get("-output"));
            printer.printToFile(initializer.isParseReview);
        } else {
            // Start ui driver
            QueryProcessor ui = new QueryProcessor(hotels, reviews, invertedIndex);
            ui.processQueries();
        }
    }

    /**
     * Base on command line argument to choose the type of server.
     * If "-server" equals "raw" then run httpServer,
     * IF "-server" equals "jetty" then run jettyServer.
     * @param commandLineArgsMap command line arguments map
     * @param fusionDataCollection hotel fusion data collection
     * @throws Exception all exceptions
     */
    private static void startServer(HashMap<String, String> commandLineArgsMap, FusionDataCollection fusionDataCollection) throws Exception {
        switch (commandLineArgsMap.get("-server")) {
            case "raw":
                HttpServer httpServer = new HttpServer(PORT, fusionDataCollection);
                httpServer.addHandlerMap("/hotelInfo","HotelHandler");
                httpServer.addHandlerMap("/reviews","ReviewsHandler");
                httpServer.addHandlerMap("/index","WordHandler");
                httpServer.addHandlerMap("/weather","WeatherHandler");
                httpServer.startServer();
                break;
            case "jetty":
                JettyServer jettyServer = new JettyServer(PORT, fusionDataCollection);
                jettyServer.start();
                break;
            default:
                System.out.println("Can't find relevant sever, please check again. ");
                break;
        }
    }
}
