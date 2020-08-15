package main.java.scheduler;

import main.java.dotio.TaskGraph;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Abstract class allowing for multiple scheduler implementations executing different algorithms
 */
public abstract class Scheduler {
    TaskGraph _input;
    int _numProcessors;
    Schedule _currentState;
    Schedule _bestState;
    ThreadPoolExecutor _threadPool;
    HashMap<String, Integer> _startTimeMap = new HashMap<>();
    HashMap<String, Integer> _processorMap = new HashMap<>();
    HashMap<String, TaskNode> _taskNodeMap = new HashMap<>();
    HashMap<TaskNode, List<Edge>> _incomingEdgesMap = new HashMap<>();

    public abstract void execute();

    public HashMap<String, Integer> getStartTimeMap() {
        return _startTimeMap;
    }

    public HashMap<String, Integer> getProcessorMap() {
        return _processorMap;
    }

    public int getNumProcessors() {
        return _numProcessors;
    }
}
