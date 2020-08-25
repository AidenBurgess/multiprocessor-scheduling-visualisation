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
    private long _scheduleRefreshRate = 1000;
    private long _statsRefreshRate = 1000;
    private long _graphRefreshRate = 1000;
    private SystemPerformanceRetriever _performanceRetriever;
    private InformationHolder _informationHolder = VisualisationDriver.getInformationHolder();


    public InformationPoller(DisplayUpdater displayUpdater) {
        _displayUpdater = displayUpdater;
        _performanceRetriever = new SystemPerformanceRetriever();
        startTimer();
    }

    /**
     * starts the timer for the total time and updates every 10 milliseconds.
     */
    private void startTimer() {

        _timer = new Timer();
        _timer.schedule(new ScheduleUpdateTask(), _scheduleRefreshRate, 1000);
        _timer.schedule(new GraphUpdateTask(), _graphRefreshRate, 1000);
        _timer.schedule(new StatsUpdateTask(), _statsRefreshRate, 1000);

    }

}
