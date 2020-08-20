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
    int activeBranches = 0;
    int totalStates = 0;
    int completeSchedules = 0;
    int currentBound = 0;
    TaskGraph taskGraph;
    State currentState, bestState;

    public InformationHolder(TaskGraph taskGraph) {
        this.taskGraph = taskGraph;
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

    public void incrementCompleteSchedule() {
        completeSchedules++;
    }

    public void setCurrentBound(int bound) {
        currentBound = bound;
    }

    public int getCurrentBound() {
        return currentBound;
    }

    public void setBestState(State state) {
        bestState = state;
    }

    public HashMap<String, Integer> getBestStartTimeMap() {
        HashMap<String, Integer> bestStartTimeMap = new HashMap<>();
        for (int i = 0; i < bestState.numTasks; i++) {
            Task task = taskGraph.getTasks().get(i);
            bestStartTimeMap.put(task.getName(), bestState.taskEndTime[i] - task.getTaskTime());
        }
        return bestStartTimeMap;
    }

    public HashMap<String, Integer> getBestProcessorMap() {
        HashMap<String, Integer> bestProcessorMap = new HashMap<>();
        for (int i = 0; i < bestState.numTasks; i++) {
            Task task = taskGraph.getTasks().get(i);
            bestProcessorMap.put(task.getName(), bestState.assignedProcessorId[i]);
        }
        return bestProcessorMap;
    }
}
