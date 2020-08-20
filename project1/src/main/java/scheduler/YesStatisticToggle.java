package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

public class YesStatisticToggle implements StatisticToggle {
    @Override
    public DFS getDFS(State state, Bound bound, ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList, ArrayList<Integer> taskTimes, InformationHolder informationHolder) {
        return new StatisticDFS(state, bound, revAdjList, taskTimes, informationHolder);
    }
}
