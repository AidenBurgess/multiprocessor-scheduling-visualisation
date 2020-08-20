package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

public interface StatisticToggle {
    public DFS getDFS(State state, Bound bound, ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList, ArrayList<Integer> taskTimes);
}
