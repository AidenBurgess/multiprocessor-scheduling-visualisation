package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

public class MinimalDFS extends DFS {
    public MinimalDFS(State state, Bound bound, ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList, ArrayList<Integer> taskTimes, InformationHolder informationHolder) {
        super(state, bound, revAdjList, taskTimes, informationHolder);
    }

    @Override
    protected void onDFSEntry() {
        // A minimal DFS would do nothing
    }

    @Override
    protected void onDFSExit() {
        // A minimal DFS would do nothing
    }

    @Override
    protected void onCompleteSchedule() {
        // A minimal DFS would update the best schedule only
        informationHolder.setBestState(state.copy());
    }
}
