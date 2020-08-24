package main.java;

import main.java.commandparser.CommandParserException;
import main.java.commandparser.Config;
import main.java.commandparser.CommandParser;
import main.java.dotio.DotIO;
import main.java.dotio.DotIOException;
import main.java.dotio.TaskGraph;
import main.java.scheduler.InformationHolder;
import main.java.scheduler.Scheduler;
import main.java.scheduler.VariableScheduler;
import main.java.validitychecker.ValidityChecker;
import main.java.visualisation.VisualisationDriver;

import java.io.FileNotFoundException;

import java.util.HashMap;

public class Driver {

    private final static int EXIT_FAILURE = 1;

    public static void main(String[] args) {

        // read the arguments and gets the config object with all attributes
        Config config;
        try {
            config = CommandParser.parse(args);
        } catch (CommandParserException e) {
            System.out.println(e.getMessage());
            return;
        }

        // read the file out from the input file
        TaskGraph taskGraph = readTaskGraph(config);

        // create a scheduler with correct statistics/processors arguments
        Scheduler scheduler = new VariableScheduler(taskGraph, config.getNumProcessors(), config.hasVisualisation(), config.getNumParallelCores());

        // if the config has visualisation, run the FX thread
        if (config.hasVisualisation()) {
            startVisualisationThread(scheduler.getInformationHolder(), taskGraph, config.getNumProcessors());
        }

        scheduler.execute(); // blocks until finished. the information can be retrieved from scheduler.getInformationHolder().

        InformationHolder informationHolder = scheduler.getInformationHolder();

        // check for the validity.
        ValidityChecker validityChecker = new ValidityChecker(taskGraph.getTasks(), taskGraph.getDependencies(), informationHolder.getBestProcessorMap(), informationHolder.getBestStartTimeMap());
        validityChecker.check();

        writeDotFile(informationHolder, taskGraph, config);
    }

    private static void startVisualisationThread(InformationHolder informationHolder, TaskGraph taskGraph, int numProcessors) {
        new Thread(() -> {
            VisualisationDriver.main(informationHolder, taskGraph, numProcessors);
        }).start();
    }

    /**
     * Reads the config file and converts the input file to a task graph to be used by other methods/classes.
     * @param config The Config object that should include the input file name.
     * @return TaskGraph The TaskGraph object that has been read and parsed from the input file.
     */
    private static TaskGraph readTaskGraph(Config config) {
        TaskGraph taskGraph = null;
        try {
            taskGraph = DotIO.read(config.getInputFileName());
        } catch (DotIOException e) {
            System.err.println("Error with dot syntax of input file: " + e.getMessage());
            System.exit(EXIT_FAILURE);
        }
        return taskGraph;
    }

    /**
     * Writes the output information to a DOT file.
     * @param informationHolder the information instance from the Scheduler.
     * @param taskGraph the original taskGraph from the input.
     * @param config the original config settings.
     */
    private static void writeDotFile(InformationHolder informationHolder, TaskGraph taskGraph, Config config) {
        HashMap<String, Integer> startTimeMap = informationHolder.getBestStartTimeMap();
        HashMap<String, Integer> processorMap = informationHolder.getBestProcessorMap();

        try {
            DotIO.write(config.getOutputFileName(), taskGraph, startTimeMap, processorMap);
        } catch (DotIOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
            System.exit(1);
        }
    }
}
