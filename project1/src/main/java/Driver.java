package main.java;

import main.java.commandparser.Config;
import main.java.commandparser.CommandParser;
import main.java.dotio.DotIO;
import main.java.dotio.DotIOException;
import main.java.dotio.TaskGraph;
import main.java.scheduler.BaseScheduler;
import main.java.scheduler.Scheduler;
import main.java.visualisation.FXController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class Driver {
    public static void main(String[] args) {

        // read the arguments and gets the config object with all attributes
        Config config = CommandParser.parse(args);

        // read the file out from the input file
        TaskGraph taskGraph = null;
        try {
            taskGraph = DotIO.read(new BufferedReader(new FileReader(config.inputFileName)));
        } catch (FileNotFoundException e) {
            System.err.println("File does not exist");
        } catch (DotIOException e) {
            System.err.println("Error with dot syntax: " + e.getMessage());
        }

        // create a scheduler with the number of processors
        Scheduler scheduler = new BaseScheduler(taskGraph, config.numProcessors);

        // Uncomment this to force visualisation on
        config.hasVisualisation = true;
        if (config.hasVisualisation) {
            startVisualisationThread(scheduler);
        }

        scheduler.execute(); // blocks until finished, can be queried by fxcontroller

        HashMap<String, Integer> startTimeMap = scheduler.getStartTimeMap();
        HashMap<String, Integer> processorMap = scheduler.getProcessorMap();

        try {
            DotIO.write(config.outputFileName, taskGraph, startTimeMap, processorMap);
        } catch (DotIOException e) {
            System.err.println(e.getMessage());
        }

    }

    public static void startVisualisationThread(Scheduler scheduler) {
        new Thread(() -> {
            FXController.main(scheduler);
        }).start();
    }
}
