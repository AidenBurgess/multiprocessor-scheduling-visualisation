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
    private long activeBranches = 0;
    private long totalStates = 0;
    private long completeStates = 0;
    private long currentBound = 0;
    private TaskGraph taskGraph;
    private State currentState, bestState;

    public InformationHolder(TaskGraph taskGraph) {
        this.taskGraph = taskGraph;
    }

    public long getActiveBranches() {
        return activeBranches;
    }

    public long getTotalStates() {
        return totalStates;
    }

    public long getCompleteStates() {
        return completeStates;
    }

    public void incrementActiveBranches() {
        activeBranches++;
    }

    public void incrementTotalStates() {
        totalStates++;
    }

    public void decrementActiveBranches() {
        activeBranches--;
    }

    public void incrementCompleteStates() {
        completeStates++;
    }

    public void setCurrentBound(int bound) {
        currentBound = bound;
    }

    public long getCurrentBound() {
        return currentBound;
    }

    public void setBestState(State state) {
        bestState = state;
    }

    public void setCurrentState(State state) {
        currentState = state;
    }

    public HashMap<String, Integer> getCurrentStartTimeMap() {
        return getStartTimeMap(currentState);
    }

    public HashMap<String, Integer> getBestStartTimeMap() {
        return getStartTimeMap(bestState);
    }

    private HashMap<String, Integer> getStartTimeMap(State state) {
        HashMap<String, Integer> currentStartTimeMap = new HashMap<>();
        for (int i = 0; i < state.numTasks; i++) {
            Task task = taskGraph.getTasks().get(i);
            currentStartTimeMap.put(task.getName(), state.taskEndTime[i] - task.getTaskTime());
        }
        return currentStartTimeMap;
    }

    public HashMap<String, Integer> getCurrentProcessorMap() {
        return getProcessorMap(currentState);
    }

    public HashMap<String, Integer> getBestProcessorMap() {
        return getProcessorMap(bestState);
    }

    private HashMap<String, Integer> getProcessorMap(State state) {
        HashMap<String, Integer> currentProcessorMap = new HashMap<>();
        for (int i = 0; i < state.numTasks; i++) {
            Task task = taskGraph.getTasks().get(i);
            currentProcessorMap.put(task.getName(), state.assignedProcessorId[i] + 1); // 1-indexed
        }
        return currentProcessorMap;
    }



}
