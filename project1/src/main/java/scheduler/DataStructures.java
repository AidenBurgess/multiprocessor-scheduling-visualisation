package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Contains all the data structures used throughout the DFS.
 * Initialised in VariableScheduler
 */
public class DataStructures {
    private ArrayList<ArrayList<Pair<Integer, Integer>>> _adjList, _revAdjList;
    private ArrayList<Integer> _topologicalIndex, _taskWeights;

    public ArrayList<ArrayList<Pair<Integer, Integer>>> getAdjList() {
        return _adjList;
    }

    public void setAdjList(ArrayList<ArrayList<Pair<Integer, Integer>>> adjList) {
        _adjList = adjList;
    }

    public ArrayList<ArrayList<Pair<Integer, Integer>>> getRevAdjList() {
        return _revAdjList;
    }

    public void setRevAdjList(ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList) {
        _revAdjList = revAdjList;
    }

    public ArrayList<Integer> getTopologicalIndex() {
        return _topologicalIndex;
    }

    public void setTopologicalIndex(ArrayList<Integer> topologicalIndex) {
        _topologicalIndex = topologicalIndex;
    }

    public ArrayList<Integer> getTaskWeights() {
        return _taskWeights;
    }

    public void setTaskWeights(ArrayList<Integer> taskWeights) {
        _taskWeights = taskWeights;
    }
}
