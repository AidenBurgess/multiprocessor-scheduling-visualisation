package main.java.scheduler;

import main.java.dotio.TaskGraph;

import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class Scheduler {
    TaskGraph input;
    public int numProcessors;
    Schedule currentState;
    Schedule bestState;
    ThreadPoolExecutor threadPool;
    public abstract void execute();
    public abstract HashMap<String, Integer> getStartTimeMap();
    public abstract HashMap<String, Integer> getProcessorMap();
}
