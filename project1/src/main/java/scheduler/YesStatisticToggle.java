package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

public class YesStatisticToggle implements StatisticToggle {
    @Override
    public DFS getDFS(State state, Bound bound, DataStructures dataStructures, InformationHolder informationHolder) {
        return new StatisticDFS(state, bound, dataStructures, informationHolder);
    }
}
