package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * This interface provides method(s) with different implementations, depending on if the Scheduler
 * wishes for Statistics to be recorded.
 */
public interface StatisticToggle {
    DFS getDFS(State state, Bound bound, ArrayList<ArrayList<Pair<Integer, Integer>>> adjList, ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList, ArrayList<Integer> taskTimes, InformationHolder informationHolder);
}
