package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Provides a minimal implementation of DFSListener.
 * Only responsible for updating the best found state.
 */
public class MinimalDFSListener implements DFSListener {
    protected InformationHolder _informationHolder;

    public MinimalDFSListener(InformationHolder informationHolder) {
        _informationHolder = informationHolder;
    }

    @Override
    public void onDFSEntry() {
        // Do nothing
    }

    @Override
    public void onDFSExit() {
        // Do nothing
    }

    @Override
    public void onCompleteSchedule(State state, Bound bound) {
        // Update the best state
        _informationHolder.setBestState(state.copy());
        _informationHolder.setCurrentBound(bound.getBound());
    }

    @Override
    public void onPartialSchedule(State state, Bound bound) {
        // Do nothing
    }
}
