package main.java.scheduler;

import main.java.dotio.TaskGraph;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Abstract class allowing for multiple scheduler implementations executing different algorithms
 */
public interface Scheduler {
    public void execute();

    public HashMap<String, Integer> getCurrentStartTimeMap();
    public HashMap<String, Integer> getCurrentProcessorMap();

    public HashMap<String, Integer> getBestStartTimeMap();
    public HashMap<String, Integer> getBestProcessorMap();

    // Statistics
    public int getCurrentBound();
    public int getTotalStatesVisited();
    public int getCompleteStatesVisited();
    public int getActiveBranches();
}
