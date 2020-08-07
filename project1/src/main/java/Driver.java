package main.java;

import main.java.commandparser.Config;
import main.java.commandparser.CommandParser;
import main.java.dotio.DotIO;
import main.java.dotio.TaskGraph;
import main.java.scheduler.BaseScheduler;
import main.java.scheduler.Scheduler;
import main.java.visualisation.FXController;

import java.util.HashMap;

public class Driver {
    public static void main(String[] args) {
        Config config = CommandParser.parse(args);
        TaskGraph taskGraph = DotIO.read(config.inputFileName);

        Scheduler scheduler = new BaseScheduler(taskGraph);

        // Uncomment this to force visualisation on
        config.hasVisualisation = true;
        if (config.hasVisualisation) {
            startVisualisationThread(scheduler);
        }

        scheduler.execute();

        HashMap<String, Integer> startTimeMap = scheduler.getStartTimeMap();
        HashMap<String, Integer> processorMap = scheduler.getProcessorMap();

        DotIO.write(config.outputFileName, taskGraph, startTimeMap, processorMap);
    }

    public static void startVisualisationThread(Scheduler scheduler) {
        new Thread(() -> {
            FXController.main(scheduler);
        }).start();
    }
}
