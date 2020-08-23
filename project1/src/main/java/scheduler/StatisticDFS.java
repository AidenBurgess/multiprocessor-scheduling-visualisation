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
        _informationHolder.incrementActiveBranches();
        _informationHolder.incrementTotalStates();

        /**
         * todo this line currently sets the current state on every new DFS entry.
         * this is INCREDIBLY inefficient.
         */
        _informationHolder.setCurrentState(_state.copy());
    }

    @Override
    protected void onDFSExit() {
        _informationHolder.decrementActiveBranches();
    }

    @Override
    protected void onCompleteSchedule() {
        super.onCompleteSchedule(); // this method updates the best state in the InformationHolder

        _informationHolder.incrementCompleteStates();
    }
}
