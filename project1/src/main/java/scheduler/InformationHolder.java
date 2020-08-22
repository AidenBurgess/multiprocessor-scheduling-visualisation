package main.java.scheduler;

import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.HashMap;

/**
 * Provides a way for other classes to access the Scheduler's information.
 * Acts as a middle-ground. Scheduler class will update this holder's information, and external classes
 * can retrieve information from this holder.
 *
 * Responsible for managing thread-safety if relevant.
 *
 */
public class InformationHolder {
    private long _activeBranches = 0;
    private long _totalStates = 0;
    private long _completeStates = 0;
    private long _currentBound = 0;
    private TaskGraph _taskGraph;
    private State _currentState, _bestState;

    public InformationHolder(TaskGraph taskGraph) {
        this._taskGraph = taskGraph;
    }

    public long getActiveBranches() {
        return _activeBranches;
    }

    public long getTotalStates() {
        return _totalStates;
    }

    public long getCompleteStates() {
        return _completeStates;
    }

    public void incrementActiveBranches() {
        _activeBranches++;
    }

    public void incrementTotalStates() {
        _totalStates++;
    }

    public void decrementActiveBranches() {
        _activeBranches--;
    }

    public void incrementCompleteStates() {
        _completeStates++;
    }

    public void setCurrentBound(int bound) {
        _currentBound = bound;
    }

    public long getCurrentBound() {
        return _currentBound;
    }

    public void setBestState(State state) {
        _bestState = state;
    }

    public void setCurrentState(State state) {
        _currentState = state;
    }

    public HashMap<String, Integer> getCurrentStartTimeMap() {
        return getStartTimeMap(_currentState);
    }

    public HashMap<String, Integer> getBestStartTimeMap() {
        return getStartTimeMap(_bestState);
    }

    private HashMap<String, Integer> getStartTimeMap(State state) {
        HashMap<String, Integer> currentStartTimeMap = new HashMap<>();
        for (int i = 0; i < state._numTasks; i++) {
            Task task = _taskGraph.getTasks().get(i);
            currentStartTimeMap.put(task.getName(), state._taskEndTime[i] - task.getTaskTime());
        }
        return currentStartTimeMap;
    }

    public HashMap<String, Integer> getCurrentProcessorMap() {
        return getProcessorMap(_currentState);
    }

    public HashMap<String, Integer> getBestProcessorMap() {
        return getProcessorMap(_bestState);
    }

    private HashMap<String, Integer> getProcessorMap(State state) {
        HashMap<String, Integer> currentProcessorMap = new HashMap<>();
        for (int i = 0; i < state._numTasks; i++) {
            Task task = _taskGraph.getTasks().get(i);
            currentProcessorMap.put(task.getName(), state._assignedProcessorId[i] + 1); // 1-indexed
        }
        return currentProcessorMap;
    }



}
