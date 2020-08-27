package main.java.scheduler;

import javafx.util.Pair;
import main.java.dotio.Dependency;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Contains all the data structures used throughout the DFS.
 * Initialised in VariableScheduler
 */
public class DataStructures {
    private int _numTasks;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> adjList, revAdjList;
    private ArrayList<Integer> topologicalOrder, topologicalIndex, taskWeights;

    public DataStructures(TaskGraph taskGraph) {
        _numTasks = taskGraph.getTasks().size();

        // Mapping each Task object to an integer id. 0-indexed.
        HashMap<String, Integer> taskNameToIdMap = new HashMap<>();
        for (int i = 0; i < taskGraph.getTasks().size(); i++) {
            taskNameToIdMap.put(taskGraph.getTasks().get(i).getName(), i);
        }

        // Instantiating the adjacency list and reverse adjacency list.
        adjList = new ArrayList<>(_numTasks);
        revAdjList = new ArrayList<>(_numTasks);
        for (int i = 0; i < _numTasks; i++) {
            adjList.add(new ArrayList<>());
            revAdjList.add(new ArrayList<>());
        }

        // Substantiate the adjList and revAdjList.
        // The adjLists use the name -> id mapping from taskNameToIdMap
        ArrayList<Dependency> dependencies = taskGraph.getDependencies();
        int dependenciesSize = dependencies.size();
        for (int i = 0; i < dependenciesSize; i++) {
            Dependency dependency = dependencies.get(i);
            int source = taskNameToIdMap.get(dependency.getSource());
            int dest = taskNameToIdMap.get(dependency.getDest());
            adjList.get(source).add(new Pair<>(dest, dependency.getCommunicationTime()));
            revAdjList.get(dest).add(new Pair<>(source, dependency.getCommunicationTime()));
        }

        // Topological Ordering. The below code finds one valid topological ordering.
        // This also ensures that the TaskGraph input has no cyclic dependencies.
        topologicalOrder = new ArrayList<>();

        // Queue all tasks that have no initial dependencies, and track the inDegree
        boolean[] visited = new boolean[_numTasks];
        int[] inDegree = new int[_numTasks];
        LinkedList<Integer> queue = new LinkedList<>();
        for (int i = 0; i < _numTasks; i++) {
            inDegree[i] = revAdjList.get(i).size();
            if (inDegree[i] == 0) {
                topologicalOrder.add(i);
                queue.push(i);
                visited[i] = true;
            }
        }

        // Take off tasks one by one, and update the inDegree
        while (!queue.isEmpty()) {
            int task = queue.poll();

            ArrayList<Pair<Integer, Integer>> revTask = adjList.get(task);
            int numRevTasks = revTask.size();
            for (int i = 0; i < numRevTasks; i++) {
                Pair<Integer, Integer> dependency = revTask.get(i);
                int child = dependency.getKey();
                inDegree[child]--;
                if (inDegree[child] == 0 && !visited[child]) {
                    topologicalOrder.add(child);
                    visited[child] = true;
                    queue.push(child);
                }
            }
        }

        // If ind != n, there are some tasks that have dependencies on each other.
        if (topologicalOrder.size() != _numTasks) throw new RuntimeException("No topological ordering found"); // todo what we want to happen?

        topologicalIndex = new ArrayList<>();
        for (int i = 0; i < _numTasks; i++) topologicalIndex.add(0);

        for (int i = 0; i < _numTasks; i++) {
            topologicalIndex.set(topologicalOrder.get(i), i);
        }

        taskWeights = new ArrayList<>();
        for (int i = 0; i < _numTasks; i++) {
            taskWeights.add(taskGraph.getTasks().get(i).getTaskTime());
        }
    }

    public int getNumTasks() {
        return _numTasks;
    }

    public ArrayList<ArrayList<Pair<Integer, Integer>>> getAdjList() {
        return adjList;
    }

    public ArrayList<ArrayList<Pair<Integer, Integer>>> getRevAdjList() {
        return revAdjList;
    }

    public ArrayList<Integer> getTopologicalOrder() {
        return topologicalOrder;
    }

    public ArrayList<Integer> getTopologicalIndex() {
        return topologicalIndex;
    }

    public ArrayList<Integer> getTaskWeights() {
        return taskWeights;
    }
}
