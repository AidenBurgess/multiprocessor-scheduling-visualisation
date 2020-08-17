package main.java.commandparser;

/**
 * This class enables parsing of the command and flags passed (if any) when the program is called from the command line
 */
public class CommandParser {

    /**
     * Reads command line arguments and creates a Config object
     * @param args
     * @return config - a Config object which has information essential for running the program
     */
    public static Config parse(String[] args) {
        Config config = new Config();
        config.setInputFileName(args[0]);
        config.setNumProcessors(Integer.parseInt(args[1]));

        int i = 2;
        while (i < args.length) {
            switch (args[i]) {
                case "-v":
                    config.setHasVisualisation(true);
                    break;
                case "-p":
                    config.setParallelised(true);
                    config.setNumParallelCores(Integer.parseInt(args[i+1]));
                    i++;
                    break;
                case "-o":
                    config.setOutputFileName(args[i+1]);
                    i++;
                    break;
                default:
                    System.err.println("Unknown configuration: " + args[i]);
            }
            i++;
        }

        if (config.getOutputFileName() == null) {
            config.setDefaultOutputFileName();
        }
        return config;
    }

}
