package main.java.commandparser;

import main.java.exception.CommandParserException;

/**
 * This class enables parsing of the command and flags passed (if any) when the program is called from the command line
 */
public class CommandParser {
    public static final String invalidMessage = "Invalid arguments. For help, try --help";
    public static final String helpMessage = "Help Menu:\nUsage: java -jar path/to/scheduler.jar inputFile.dot PROCESSORS [-o outputFile.dot | -v | -p PARALLEL_CORES | --help]";
    /**
     * Reads command line arguments and creates a Config object
     * @param args
     * @return config - a Config object which has information essential for running the program
     */
    public static Config parse(String[] args) {
        Config config = new Config();

        // If there is any --help at all, help will be shown
        for (String arg : args) {
            if (arg.equals("--help")) {
                throw new CommandParserException(helpMessage);
            }
        }

        // If there are not enough arguments
        if (args.length < 2) {
            // Prompt them to use --help
            throw new CommandParserException(invalidMessage);
        }

        // Read input file name and number of processors
        config.setInputFileName(args[0]);
        try {
            config.setNumProcessors(Integer.parseInt(args[1]));
        } catch (NumberFormatException e) {
            throw new CommandParserException("The number of processes must be an integer.");
        }

        // iterate and find any optional variables.
        int i = 2;
        while (i < args.length) {
            switch (args[i]) {
                case "-v":
                    config.setHasVisualisation(true);
                    break;
                case "-p":
                    config.setParallelised(true);
                    try {
                        config.setNumParallelCores(Integer.parseInt(args[i + 1]));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new CommandParserException("The number of parallel cores is not specified.");
                    } catch (NumberFormatException e) {
                        throw new CommandParserException("The number of parallel cores must be an integer.");
                    }
                    i++;
                    break;
                case "-o":
                    try {
                        config.setOutputFileName(args[i + 1]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new CommandParserException("The output file location is not specified.");
                    }
                    i++;
                    break;
                default:
                    throw new CommandParserException(invalidMessage);
            }
            i++;
        }

        if (config.getOutputFileName() == null) {
            config.setDefaultOutputFileName();
        }

        return config;
    }
}
