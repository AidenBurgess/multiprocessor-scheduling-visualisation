package main.java.commandparser;

/**
 * Take a string args from main and will read the number of processors, whether to parallelise
 * and will be try or false.
 */
public class CommandParser {
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

        System.out.println("Command has been parsed!");
        return config;
    }

}
