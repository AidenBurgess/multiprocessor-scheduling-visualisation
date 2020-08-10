package main.java.commandparser;

/**
 * Take a string args from main and will read the number of processors, whether to parallelise
 * and will be try or false.
 */
public class CommandParser {
    public static Config parse(String[] args) {
        Config config = new Config();
        config.inputFileName = args[0];
        config.numProcessors = Integer.parseInt(args[1]);

        int i = 2;
        while (i < args.length) {
            if (args[i].equals("-v")) config.hasVisualisation = true;
            if (args[i].equals("-p")) {
                config.isParallelised = true;
                config.numParallelCores = Integer.parseInt(args[i+1]);
                i++;
            }
            if (args[i].equals("-o")) {
                config.outputFileName = args[i+1];
                i++;
            }
            i++;
        }

        if (config.outputFileName == null) {
            config.outputFileName = config.inputFileName.split(".dot")[0] + "-output.dot";
        }

        System.out.println("Command has been parsed!");
        return config;
    }

}
