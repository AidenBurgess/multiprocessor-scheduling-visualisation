package main.java.visualisation;

import javafx.application.Platform;
import main.java.dataretriever.SystemPerformanceRetriever;
import main.java.scheduler.InformationHolder;
import main.java.scheduler.ScheduleStateMaps;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A class that sets up a timer to poll the various information sources for the visualisation.
 */
public class InformationPoller {

    // The timer object that update tasks will be scheduled on
    private Timer _timer;
    // The display updater that contains the methods for updating/refreshing UI components
    private DisplayUpdater _displayUpdater;

    //The initial delay before beginning the polling cycle
    private long _displayUpdateDelay = 0;

    //The various periods for the polling cycles of different tasks
    private long _schedulesUpdatePeriod = 100;
    private long _cpuRamUpdatePeriod = 500;
    private long _statsUpdatePeriod = 100;

    //The objects that are used to retrieve information about the system and scheduler for the UI.
    private SystemPerformanceRetriever _performanceRetriever;
    private InformationHolder _informationHolder = VisualisationDriver.getInformationHolder();

    /**
     * Constructs a poller object with the specified updater and performance retriever. The timer for this poller is
     * started upon construction.
     *
     * @param displayUpdater       The {@link DisplayUpdater} object used to update/refresh UI components.
     * @param performanceRetriever The {@link SystemPerformanceRetriever} object used to get RAM and CPU usage info.
     */
    public InformationPoller(DisplayUpdater displayUpdater, SystemPerformanceRetriever performanceRetriever) {
        _displayUpdater = displayUpdater;
        _performanceRetriever = performanceRetriever;
        startTimer();
    }

    /**
     * Starts the poller's {@link Timer} which runs scheduled tasks with a delay and periods. The delays/periods are
     * constant and remain unchanged throughout execution.
     */
    private void startTimer() {
        //Plot initial points on the CPU and RAM line charts before the timer starts.
        _displayUpdater.refreshCPUChart(_performanceRetriever.getCPUUsagePercent());
        _displayUpdater.refreshRAMChart(_performanceRetriever.getRAMUsageGigaBytes());

        //Instantiate the timer and schedule all tasks on it. The timer starts running when it is constructed.
        _timer = new Timer();
        _timer.schedule(new ScheduleUpdateTask(), _displayUpdateDelay, _schedulesUpdatePeriod);
        _timer.schedule(new GraphUpdateTask(), _displayUpdateDelay, _cpuRamUpdatePeriod);
        _timer.schedule(new StatsUpdateTask(), _displayUpdateDelay, _statsUpdatePeriod);

    }

    /**
     * A task to be scheduled on the {@link InformationPoller}'s timer. Its job is to update the current and best schedules on
     * the UI with information retrieved from the {@link InformationHolder}.
     */
    private class ScheduleUpdateTask extends TimerTask {
        @Override
        public void run() {
            Platform.runLater(() -> {
                // Retrieving the current and the best schedule information
                ScheduleStateMaps scheduleStateMaps = _informationHolder.getScheduleStateMaps();

                HashMap<String, Integer> currentProcessorMap = scheduleStateMaps.getCurrentProcessorMap();
                HashMap<String, Integer> bestProcessorMap = scheduleStateMaps.getBestProcessorMap();
                HashMap<String, Integer> currentStartTimeMap = scheduleStateMaps.getCurrentStartTimeMap();
                HashMap<String, Integer> bestStartTimeMap = scheduleStateMaps.getBestStartTimeMap();
                long currentBound = _informationHolder.getCurrentBound();

                // Refresh the schedule charts using the retrieved information.
                _displayUpdater.refreshScheduleCharts(currentProcessorMap, bestProcessorMap, currentStartTimeMap, bestStartTimeMap, currentBound);
            });
        }
    }

    /**
     * A task to be scheduled on the {@link InformationPoller}'s timer. Its job is to update the CPU usage graph and the RAM
     * usage graph with information retrieved from the {@link SystemPerformanceRetriever}.
     */
    private class GraphUpdateTask extends TimerTask {
        @Override
        public void run() {
            // Run updating methods in GUI thread
            Platform.runLater(() -> {
                _displayUpdater.refreshCPUChart(_performanceRetriever.getCPUUsagePercent());
                _displayUpdater.refreshRAMChart(_performanceRetriever.getRAMUsageGigaBytes());
            });
        }
    }

    /**
     * A task to be scheduled on the {@link InformationPoller}'s timer. Its job is to update the displayed statistics on the UI
     * with information retrieved from the {@link InformationHolder}
     */
    private class StatsUpdateTask extends TimerTask {
        @Override
        public void run() {
            // Retrieve scheduler statistics from InformationHolder
            long visitedBranches = _informationHolder.getTotalStates();
            long completedSchedules = _informationHolder.getCompleteStates();
            long activeBranches = _informationHolder.getActiveBranches();

            // Run updating methods in GUI thread
            Platform.runLater(() -> {
                _displayUpdater.updateStatistics(visitedBranches, completedSchedules, activeBranches);

                //If the scheduler has competed the search, notify the DisplayUpdater so that it stops updating the displayed time.
                if ((_informationHolder.getSchedulerStatus() == _informationHolder.FINISHED)) {
                    _displayUpdater.stopTimer();
                }
            });
        }
    }
}
