package main.java.visualisation;

import javafx.application.Platform;
import main.java.dataretriever.SystemPerformanceRetriever;
import main.java.scheduler.InformationHolder;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class InformationPoller {

    private Timer _timer;
    private DisplayUpdater _displayUpdater;
    private long _displayUpdateDelay = 0;
    private long _schedulesUpdatePeriod = 100;
    private long _cpuRamUpdatePeriod = 500;
    private long _statsUpdatePeriod = 100;
    private SystemPerformanceRetriever _performanceRetriever;
    private InformationHolder _informationHolder = VisualisationDriver.getInformationHolder();


    public InformationPoller(DisplayUpdater displayUpdater) {
        _displayUpdater = displayUpdater;
        _performanceRetriever = new SystemPerformanceRetriever();
        startTimer();
    }

    /**
     * starts the timer which runs scheduled tasks with a delay and a period
     * - Delay: initial delay before a task is first performed
     * - Period: delay period between two occurences of the same task
     */
    private void startTimer() {

        _displayUpdater.refreshCPUChart(_performanceRetriever.getCPUUsagePercent());
        _displayUpdater.refreshRAMChart(_performanceRetriever.getRAMUsageGigaBytes());
        _timer = new Timer();
        _timer.schedule(new ScheduleUpdateTask(), _displayUpdateDelay, _schedulesUpdatePeriod);
        _timer.schedule(new GraphUpdateTask(), _displayUpdateDelay, _cpuRamUpdatePeriod);
        _timer.schedule(new StatsUpdateTask(), _displayUpdateDelay, _statsUpdatePeriod);

    }


    private class ScheduleUpdateTask extends TimerTask {

        @Override
        public void run() {
            Platform.runLater(() -> {
                // Retrieving the current and the best schedule information
                HashMap<String, Integer> currentProcessorMap = _informationHolder.getCurrentProcessorMap();
                HashMap<String, Integer> bestProcessorMap = _informationHolder.getBestProcessorMap();
                HashMap<String, Integer> currentStartTimeMap = _informationHolder.getCurrentStartTimeMap();
                HashMap<String, Integer> bestStartTimeMap = _informationHolder.getBestStartTimeMap();

                _displayUpdater.refreshScheduleCharts(currentProcessorMap, bestProcessorMap, currentStartTimeMap, bestStartTimeMap);

            });

        }
    }


    private class GraphUpdateTask extends TimerTask {

        @Override
        public void run() {
            // queue tasks on the other thread
            Platform.runLater(() -> {
                _displayUpdater.refreshCPUChart(_performanceRetriever.getCPUUsagePercent());
                _displayUpdater.refreshRAMChart(_performanceRetriever.getRAMUsageGigaBytes());
            });
        }
    }


    private class StatsUpdateTask extends TimerTask {

        @Override
        public void run() {
            long visitedBranches =  _informationHolder.getTotalStates();
            long completedSchedules = _informationHolder.getCompleteStates();
            long activeBranches = _informationHolder.getActiveBranches();
            _displayUpdater.updateStatistics(visitedBranches, completedSchedules, activeBranches);

            if ((_informationHolder.getSchedulerStatus() == _informationHolder.FINISHED)) {
                _displayUpdater.stopTimer();
            }

        }
    }
}
