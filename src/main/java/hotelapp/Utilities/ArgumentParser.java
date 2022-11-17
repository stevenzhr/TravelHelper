package hotelapp.Utilities;

import java.util.HashMap;

public class ArgumentParser {

    private final String[] requiredArguments;
    private HashMap<String, String> commandLineArgsMap;

    /**
     * Constructor
     * @param requiredArguments list of required arguments
     */
    public ArgumentParser(String[] requiredArguments) {
        commandLineArgsMap = new HashMap<>();
        this.requiredArguments = requiredArguments;
    }

    /**
     * Parser for command line arguments.
     * @param args command line arguments
     */
    public HashMap<String, String> parseArguments(String[] args) {
        // If args has no arguments, exit
        if (args.length == 0) {
            System.out.println("Can not running without input arguments. ");
            System.exit(0);
        }
        // Use two pointer to select command argument and parameter. Then save them to commandLineArgsMap
        commandLineArgsMap = new HashMap<>();
        int i = 0, j = 0;
        while(j < args.length) {
            j = i + 1;
            StringBuilder commandArg = new StringBuilder();
            while(args[j].indexOf("-") != 0) {
                commandArg.append(args[j]).append(" ");
                j++;
                if (j >= args.length)
                    break;
            }
            commandLineArgsMap.put(args[i], commandArg.substring(0,commandArg.length() - 1));
            i = j;
        }
        // Check required arguments
        for (String str : requiredArguments) {
            if (!commandLineArgsMap.containsKey(str)) {
                System.out.println("Required arguments missing. [" + str +"]");
                System.exit(0);
            }
        }
        return commandLineArgsMap;
    }
}
