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

        try {

            // if there are no arguments
            if (args.length == 0) {
                // prompt user that they can use --help
                String helpMessage = "Too few arguments\nYou can add --help for help with the jar arguments";
                System.out.println(helpMessage);
                System.exit(1);

            } else if (args.length == 1) {
                // check if it is --help
                if (args[0].equals("--help")) {
                    helpMenu();
                } else {
                    String helpMessage = "Too few arguments\nYou can add --help for help with the jar arguments";
                    System.out.println(helpMessage);
                    System.exit(1);
                }
            } else {
                if (!args[0].equals("--help") && !args[1].equals("--help")) {

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
                                config.setNumParallelCores(Integer.parseInt(args[i + 1]));
                                i++;
                                break;
                            case "-o":
                                config.setOutputFileName(args[i + 1]);
                                i++;
                                break;
                            case "-cv":
                                // check if the output is valid
                                config.setCheckValid(true);
                                break;
                            case "--help":
                                helpMenu();

                            default:
                                String helpMessage = "Illegal option: You entered '".concat(args[i]).concat("'\nusage: java -jar schedular.jar inputFile.dot P [-o outputFile.dot | -v | -p | --help]");
                                System.out.println(helpMessage);
                                System.exit(1);
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
        } catch (NumberFormatException e) {
            String helpMessage = "The input to the number of processors is invalid, please enter a valid number\ne.g java -jar JarFile.jar inputFile.dot 2";
            System.out.println(helpMessage);
            System.exit(1);
        }

        return config;
    }

    private static void helpMenu() {
        String helpMessage = "Help Menu: \nusage: java -jar JarFile.jar inputFile.dot P [-o outputFile.dot | -v | -p | --help]";
        System.out.println(helpMessage);
        System.exit(1);
    }

}
