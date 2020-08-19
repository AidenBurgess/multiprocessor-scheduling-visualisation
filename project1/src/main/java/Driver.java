package main.java;

import main.java.commandparser.Config;
import main.java.commandparser.CommandParser;
import main.java.dotio.DotIO;
import main.java.dotio.DotIOException;
import main.java.dotio.TaskGraph;
import main.java.scheduler.BaseScheduler;
import main.java.scheduler.Scheduler;
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
        Scheduler scheduler = new BaseScheduler(taskGraph, config.getNumProcessors());

        // Uncomment this to force visualisation on
        // config.hasVisualisation = true;
        if (config.hasVisualisation()) {
            startVisualisationThread(scheduler, taskGraph, config.getNumProcessors());
            System.out.println("hello");
        }

        scheduler.execute(); // blocks until finished, can be queried by dashboardcontroller
        writeDotFile(scheduler, taskGraph, config);
    }

    private static void startVisualisationThread(Scheduler scheduler, TaskGraph taskGraph, int numProcessors) {
        new Thread(() -> {
            VisualisationDriver.main(scheduler, taskGraph, numProcessors);
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
    private static void writeDotFile(Scheduler scheduler, TaskGraph taskGraph, Config config) {
        HashMap<String, Integer> startTimeMap = scheduler.getStartTimeMap();
        HashMap<String, Integer> processorMap = scheduler.getProcessorMap();

        try {
            DotIO.write(config.getOutputFileName(), taskGraph, startTimeMap, processorMap);
        } catch (DotIOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
            System.exit(1);
        }
    }
}
