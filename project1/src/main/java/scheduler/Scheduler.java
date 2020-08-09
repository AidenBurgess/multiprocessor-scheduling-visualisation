package main.java.scheduler;

import main.java.dotio.TaskGraph;

import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Takes the graph from the dotIO.
 * currentState and bestState will be constantly updated
 *
 * @// TODO: 10/08/20 getcurrentstate, getbeststate, dfs methods.
 */
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
