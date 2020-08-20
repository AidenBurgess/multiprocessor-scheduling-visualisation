package main.java.scheduler;

import java.util.HashMap;

public class InformationHolder {
    int activeBranches = 0;
    int totalStates = 0;
    int completeSchedules = 0;

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


    public HashMap<String, Integer> getBestStartTimeMap() {
        return null;
    }

    public HashMap<String, Integer> getBestProcessorMap() {
        return null;
    }
}
