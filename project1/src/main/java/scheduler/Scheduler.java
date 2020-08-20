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
    public InformationHolder getInformationHolder();
}
