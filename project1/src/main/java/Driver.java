package main.java;

import main.java.commandparser.Config;
import main.java.commandparser.CommandParser;
import main.java.dotio.DotIO;
import main.java.dotio.DotIOException;
import main.java.dotio.TaskGraph;
import main.java.scheduler.BaseScheduler;
import main.java.scheduler.Scheduler;
import main.java.visualisation.FXController;

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
            startVisualisationThread(scheduler);
        }

        scheduler.execute(); // blocks until finished, can be queried by fxcontroller
        writeDotFile(scheduler, taskGraph, config);
    }

    private static void startVisualisationThread(Scheduler scheduler) {
        new Thread(() -> {
            FXController.main(scheduler);
        }).start();
    }

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
