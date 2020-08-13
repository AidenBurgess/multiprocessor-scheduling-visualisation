package main.java.scheduler;

import main.java.dotio.TaskGraph;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Takes the graph from the dotIO.
 * currentState and bestState will be constantly updated
 *
 * @// TODO: 10/08/20 getcurrentstate, getbeststate, dfs methods.
 */

// @// TODO: 13/08/20 add visibility modifiers such as public, protected etc. and think about this for all classes
public abstract class Scheduler {
    TaskGraph input;
    int numProcessors;
    Schedule currentState;
    Schedule bestState;
    ThreadPoolExecutor threadPool;
    HashMap<String, Integer> startTimeMap = new HashMap<>();
    HashMap<String, Integer> processorMap = new HashMap<>();
    HashMap<String, TaskNode> taskNodeMap = new HashMap<>();
    HashMap<TaskNode, List<Edge>> incomingEdgesMap = new HashMap<>();

    public abstract void execute();

    public HashMap<String, Integer> getStartTimeMap() {
        return startTimeMap;
    }

    public HashMap<String, Integer> getProcessorMap() {
        return processorMap;
    }
}
