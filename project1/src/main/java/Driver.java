package main.java;

import main.java.commandparser.Config;
import main.java.commandparser.CommandParser;
import main.java.dotio.DotIO;
import main.java.dotio.DotIOException;
import main.java.dotio.TaskGraph;
import main.java.scheduler.InformationHolder;
import main.java.scheduler.Scheduler;
import main.java.scheduler.VariableScheduler;
import main.java.visualisation.VisualisationDriver;

import java.io.FileNotFoundException;

import java.util.HashMap;

public class Driver {
    public static void main(String[] args) {

        // read the arguments and gets the config object with all attributes
        Config config = CommandParser.parse(args);

        // read the file out from the input file
        TaskGraph taskGraph = readTaskGraph(config);

        // create a scheduler with the number of processors
        Scheduler scheduler;

        // if the config has visualisation, recordStatistics = true for the VariableScheduler.
        if (config.hasVisualisation()) {
            scheduler = new VariableScheduler(taskGraph, config.getNumProcessors(), true, config.getNumParallelCores());
            startVisualisationThread(scheduler.getInformationHolder(), taskGraph, config.getNumProcessors());
        } else {
            scheduler = new VariableScheduler(taskGraph, config.getNumProcessors(), false, config.getNumParallelCores());
        }

        scheduler.execute(); // blocks until finished. the information can be retrieved from scheduler.getInformationHolder().

        InformationHolder informationHolder = scheduler.getInformationHolder();
        writeDotFile(informationHolder, taskGraph, config);
    }

    private static void startVisualisationThread(InformationHolder informationHolder, TaskGraph taskGraph, int numProcessors) {
        new Thread(() -> {
            VisualisationDriver.main(informationHolder, taskGraph, numProcessors);
        }).start();
    }

    /**
     * Reads the config file and converts it to a task graph to be used by other methods/classes.
     * @param config
     * @return TaskGraph
     */
    private static TaskGraph readTaskGraph(Config config) {
        TaskGraph taskGraph = null;
        try {
            taskGraph = DotIO.read(config.getInputFileName());
        } catch (FileNotFoundException e) {
            System.err.println("Error: File " + config.getInputFileName() + " does not exist");
            System.exit(1);
        } catch (DotIOException e) {
            System.err.println("Error with dot syntax of input file: " + e.getMessage());
            System.exit(1);
        }
        return taskGraph;
    }

    /**
     * Writes the output schedule to a dot file.
     * @param scheduler
     * @param taskGraph
     * @param config
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
