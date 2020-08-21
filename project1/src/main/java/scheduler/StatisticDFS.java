package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * An implementation of DFS where statistics are measured and updated in the InformationHolder instance.
 */
public class StatisticDFS extends MinimalDFS {
    public StatisticDFS(State state, Bound bound, ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList, ArrayList<Integer> taskTimes, InformationHolder informationHolder) {
        super(state, bound, revAdjList, taskTimes, informationHolder);
    }

    @Override
    protected void onDFSEntry() {
        informationHolder.incrementActiveBranches();
        informationHolder.incrementTotalStates();

//        informationHolder.setCurrentState(state.copy());
    }

    @Override
    protected void onDFSExit() {
        informationHolder.decrementActiveBranches();
    }

    @Override
    protected void onCompleteSchedule() {
        super.onCompleteSchedule(); // this method updates the best state in the InformationHolder

        informationHolder.setCurrentState(state.copy());
        informationHolder.incrementCompleteStates();
    }
}
