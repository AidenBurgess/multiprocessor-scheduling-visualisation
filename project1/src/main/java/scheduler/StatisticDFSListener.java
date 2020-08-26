package main.java.scheduler;

import javafx.util.Pair;

import javax.swing.*;
import java.util.ArrayList;

/**
 * An implementation of DFS where statistics are measured and updated in the InformationHolder instance.
 */
public class StatisticDFSListener extends MinimalDFSListener {
    InformationHolder _informationHolder;

    public StatisticDFSListener(InformationHolder informationHolder) {
        super(informationHolder);
    }

    @Override
    public void onDFSEntry() {
        super.onDFSEntry();
        _informationHolder.incrementActiveBranches();
        _informationHolder.incrementTotalStates();
    }

    @Override
    public void onDFSExit() {
        super.onDFSExit();
        _informationHolder.decrementActiveBranches();
    }

    @Override
    public void onCompleteSchedule(State state, Bound bound) {
        super.onCompleteSchedule(state, bound);
        _informationHolder.incrementCompleteStates();
    }

    @Override
    public void onPartialSchedule(State state, Bound bound) {
        super.onPartialSchedule(state, bound);
    }
}
