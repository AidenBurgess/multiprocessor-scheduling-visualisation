package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

public class NoStatisticToggle implements StatisticToggle {
    @Override
    public DFS getDFS(State state, Bound bound, DataStructures dataStructures, InformationHolder informationHolder) {
        return new MinimalDFS(state, bound, dataStructures, informationHolder);
    }
}
