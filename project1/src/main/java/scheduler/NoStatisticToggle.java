package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

public class NoStatisticToggle implements StatisticToggle {
    @Override
    public DFS getDFS(State state, Bound bound, ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList, ArrayList<Integer> taskTimes) {
        return new DFS(state, bound, revAdjList, taskTimes);
    }
}
