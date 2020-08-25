package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Provides a minimal implementation of DFS.
 * Only responsible for updating the best found state.
 */
public class MinimalDFS extends DFS {
    public MinimalDFS(State state, Bound bound, DataStructures dataStructures, InformationHolder informationHolder) {
        super(state, bound, dataStructures, informationHolder);
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
        _informationHolder.setBestState(_state.copy());
        _informationHolder.setCurrentBound(_bound.getBound());
    }
}
