package main.java.scheduler;

import main.java.dotio.TaskGraph;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Abstract class allowing for multiple scheduler implementations executing different algorithms
 */
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
