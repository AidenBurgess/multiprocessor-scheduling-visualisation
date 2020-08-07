package main.java;

import main.java.commandparser.Command;
import main.java.commandparser.CommandParser;
import main.java.dotio.DotIO;
import main.java.dotio.TaskGraph;
import main.java.scheduler.BaseScheduler;
import main.java.scheduler.Scheduler;
import main.java.visualisation.FXController;

import java.util.HashMap;

public class Driver {
    public static void main(String[] args) {
        Command command = CommandParser.parse(args);
        TaskGraph taskGraph = DotIO.read(command.inputFileName);

        Scheduler scheduler = new BaseScheduler(taskGraph);

        // Uncomment this to force visualisation on
        command.hasVisualisation = true;
        if (command.hasVisualisation) {
            startVisualisationThread(scheduler);
        }

        scheduler.execute();

        HashMap<String, Integer> startTimeMap = scheduler.getStartTimeMap();
        HashMap<String, Integer> processorMap = scheduler.getProcessorMap();

        DotIO.write(command.outputFileName, taskGraph, startTimeMap, processorMap);
    }

    public static void startVisualisationThread(Scheduler scheduler) {
        new Thread(() -> {
            FXController.main(scheduler);
        }).start();
    }
}
