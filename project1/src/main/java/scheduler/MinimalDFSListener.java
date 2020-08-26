package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Provides a minimal implementation of DFS.
 * Only responsible for updating the best found state.
 */
public class MinimalDFSListener implements DFSListener {
    InformationHolder _informationHolder;
    public MinimalDFSListener(InformationHolder informationHolder) {
        _informationHolder = informationHolder;
    }

    @Override
    public void onDFSEntry() {
        // A minimal DFS would do nothing
    }

    @Override
    public void onDFSExit() {
        // A minimal DFS would do nothing
    }

    @Override
    public void onCompleteSchedule(State state, Bound bound) {
        // A minimal DFS would update the best schedule only
        _informationHolder.setBestState(state.copy());
        _informationHolder.setCurrentBound(bound.getBound());
    }

    @Override
    public void onPartialSchedule(State state, Bound bound) {

    }
}
