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

        // if there are no arguments
        if (args.length == 0) {
            // prompt user that they can use --help
            String helpMessage = "Too few arguments\nYou can add --help for help with the jar arguments";
            exitMessage(helpMessage);
        } else if (args.length == 1) { // when there is just one argument, make sure it is only --help
            // check if it is --help
            if (args[0].equals("--help")) {
                helpMenu();
            } else {
                String helpMessage = "Too few arguments\nYou can add --help for help with the jar arguments";
                exitMessage(helpMessage);
            }
        } else { // if there is --help in the first two, show help
            if (!args[0].equals("--help") && !args[1].equals("--help")) {

                // get the filename which is the first argument
                config.setInputFileName(args[0]);

                try {
                    // set the number of processors
                    config.setNumProcessors(Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    String helpMessage = "The number of processors must be an integer.";
                    exitMessage(helpMessage);
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
                                String helpMessage = "-p argument must have an argument following for the number of processors.";
                                exitMessage(helpMessage);
                            } catch (NumberFormatException e) {
                                String helpMessage = "The argument following -p must be an integer.";
                                exitMessage(helpMessage);
                            }
                            i++;
                            break;
                        case "-o":

                            try {
                                config.setOutputFileName(args[i + 1]);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                String helpMessage = "-o argument must have an argument following for the file output name.";
                                exitMessage(helpMessage);
                            }
                            i++;
                            break;
                        case "-cv":
                            // check if the output is valid
                            config.setCheckValid(true);
                            break;
                        case "--help":
                            helpMenu();
                        default:
                            String helpMessage = "Illegal option: You entered '".concat(args[i]).concat("'\nusage: java -jar path/to/JarFile.jar inputFile.dot P [-o outputFile.dot | -v | -p | --help]");
                            exitMessage(helpMessage);
                    }
                    i++;
                }

                if (config.getOutputFileName() == null) {
                    config.setDefaultOutputFileName();
                }

            } else {
                helpMenu();
            }
        }
//        } catch (NumberFormatException e) {
//            String helpMessage = "The input to the number of processors is invalid, please enter a valid number\ne.g java -jar JarFile.jar inputFile.dot 2";
//            exitMessage(helpMessage);
//        }

        return config;
    }

    private static void helpMenu() {
        String helpMessage = "Help Menu: \nusage: java -jar path/to/JarFile.jar inputFile.dot P [-o outputFile.dot | -v | -p | --help]";
        System.out.println(helpMessage);
        System.exit(1);
    }

    private static void exitMessage(String message) {
        System.out.println(message);
        System.exit(1);
    }

}
