package main.java.scheduler;

import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.HashMap;

public class InformationHolder {
    int activeBranches = 0;
    int totalStates = 0;
    int completeSchedules = 0;
    TaskGraph taskGraph;
    boolean bestMapsAreGood = false;
    HashMap<String, Integer> bestStartTimeMap = null, bestProcessorMap = null;

    public InformationHolder(TaskGraph taskGraph) {
        this.taskGraph = taskGraph;
    }

    State currentState, bestState;

    public void incrementActiveBranches() {
        activeBranches++;
        if (activeBranches % 3 == 0) System.out.println(activeBranches);
    }

    public void incrementTotalStates() {
        totalStates++;
    }

    public void decrementActiveBranches() {
        activeBranches--;
        if (activeBranches % 3 == 0) System.out.println(activeBranches);
    }

    public void incrementCompleteSchedule() {
        completeSchedules++;
    }


    public void setBestState(State state) {
        bestMapsAreGood = false;
        bestState = state;
    }

    private void setBestMaps() {
        if (!bestMapsAreGood) {
            if (bestStartTimeMap == null) bestStartTimeMap = new HashMap<>();
            if (bestProcessorMap == null) bestProcessorMap = new HashMap<>();

            for (int i = 0; i < bestState.numTasks; i++) {
                Task task = taskGraph.getTasks().get(i);
                bestStartTimeMap.put(task.getName(), bestState.taskEndTime[i] - task.getTaskTime());
                bestProcessorMap.put(task.getName(), bestState.assignedProcessorId[i]);
            }
        }
        bestMapsAreGood = true;
    }

    public HashMap<String, Integer> getBestStartTimeMap() {
        setBestMaps();
        return bestStartTimeMap;
    }

    public HashMap<String, Integer> getBestProcessorMap() {
        setBestMaps();
        return bestProcessorMap;
    }
}
