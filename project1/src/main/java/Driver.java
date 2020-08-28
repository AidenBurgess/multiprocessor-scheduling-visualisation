package main.java;

import main.java.commandparser.Config;
import main.java.commandparser.CommandParser;
import main.java.dotio.DotIO;
import main.java.dotio.TaskGraph;
import main.java.exception.CommandParserException;
import main.java.exception.DotIOException;
import main.java.exception.SchedulerException;
import main.java.exception.ValidityCheckerException;
import main.java.scheduler.InformationHolder;
import main.java.scheduler.Scheduler;
import main.java.scheduler.VariableScheduler;
import main.java.validitychecker.ValidityChecker;
import main.java.visualisation.VisualisationDriver;

import java.util.HashMap;

/**
 * Entry to the program. Responsible for passing control through each of the main components.
 */
public class Driver {
    /**
     * Responsible for:
     * - Read command line
     * - Read DOT file
     * - Open Visualisation, if applicable
     * - Run Scheduler
     * - Write DOT file
     * When a RuntimeException is thrown, main catches it and handles it responsibly.
     *
     * @param args Arguments/Options from the command line.
     */
    public static void main(String[] args) {
        try {
            // Read the arguments from command line into config
            Config config = CommandParser.parse(args);

            // Read the file out from the input file
            TaskGraph taskGraph = DotIO.read(config.getInputFileName());

            // Create a scheduler with correct statistics/processors arguments
            Scheduler scheduler = new VariableScheduler(taskGraph, config.getNumProcessors(), config.hasVisualisation(), config.getNumParallelCores());

            // Start FX thread if applicable
            if (config.hasVisualisation()) {
                startVisualisationThread(scheduler.getInformationHolder(), taskGraph, config.getNumProcessors());
            }

            // Start searching for an optimal solution. Blocks until finished.
            // Information can be accessed from scheduler.getInformationHolder();
            scheduler.execute();

            // Retrieve solution.
            InformationHolder informationHolder = scheduler.getInformationHolder();
            HashMap<String, Integer> startTimeMap = informationHolder.getScheduleStateMaps().getBestStartTimeMap();
            HashMap<String, Integer> processorMap = informationHolder.getScheduleStateMaps().getBestProcessorMap();

            // Check if solution is valid. Throws ValidCheckerException if not.
            ValidityChecker validityChecker = new ValidityChecker(taskGraph.getTasks(), taskGraph.getDependencies(), informationHolder.getScheduleStateMaps().getBestProcessorMap(), informationHolder.getScheduleStateMaps().getBestStartTimeMap());
            validityChecker.check();

            // Writes answer to output
            DotIO.write(config.getOutputFileName(), taskGraph, startTimeMap, processorMap);

        } catch (CommandParserException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (DotIOException e) {
            System.err.println("Something went wrong while reading/writing the DOT files.");
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (SchedulerException e) {
            System.err.println("Something went wrong while trying to find an optimum schedule.");
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ValidityCheckerException e) {
            System.err.println("The result that we have produced may not be valid.");
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.err.println("An unexpected error has happened. Exiting...");
            throw e;
        }
    }

    private static void startVisualisationThread(InformationHolder informationHolder, TaskGraph taskGraph, int numProcessors) {
        new Thread(() -> {
            VisualisationDriver.main(informationHolder, taskGraph, numProcessors);
        }).start();
    }
}
