package main.java.scheduler;

import javafx.util.Pair;
import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;
import main.java.exception.SchedulerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Contains all the data structures used throughout the DFS.
 * Initialised in VariableScheduler
 */
public class DataStructures {
    private final TaskGraph _taskGraph;
    private int _numTasks;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> adjList, revAdjList;
    private ArrayList<Integer> topologicalOrder, topologicalIndex, taskWeights;
    private HashMap<String, Integer> _taskNameToIdMap;

    public DataStructures(TaskGraph taskGraph) {
        _taskGraph = taskGraph;
        checkValidWeights();

        initIdMapping();
        initAdjLists();
        initTopologicalOrderIndex();
        initTaskWeights();
    }

    private void initIdMapping() {
        _numTasks = _taskGraph.getTasks().size();

        // Mapping each Task object to an integer id. 0-indexed.
        _taskNameToIdMap = new HashMap<>();
        for (int i = 0; i < _taskGraph.getTasks().size(); i++) {
            _taskNameToIdMap.put(_taskGraph.getTasks().get(i).getName(), i);
        }
    }

    private void checkValidWeights() {
        for (Task task : _taskGraph.getTasks()) {
            if (task.getTaskTime() <= 0) { // todo check!!!
                throw new SchedulerException("The task weight must be positive.");
            }
        }
        for (Dependency dependency : _taskGraph.getDependencies()) {
            if (dependency.getCommunicationTime() < 0) {
                throw new SchedulerException("The dependency communication time must be non-negative.");
            }
        }
    }

    private void initAdjLists() {
        // Instantiating the adjacency list and reverse adjacency list.
        adjList = new ArrayList<>(_numTasks);
        revAdjList = new ArrayList<>(_numTasks);
        for (int i = 0; i < _numTasks; i++) {
            adjList.add(new ArrayList<>());
            revAdjList.add(new ArrayList<>());
        }
        // Substantiate the adjList and revAdjList.
        // The adjLists use the name -> id mapping from taskNameToIdMap
        ArrayList<Dependency> dependencies = _taskGraph.getDependencies();
        int dependenciesSize = dependencies.size();
        for (int i = 0; i < dependenciesSize; i++) {
            Dependency dependency = dependencies.get(i);
            int source = _taskNameToIdMap.get(dependency.getSource());
            int dest = _taskNameToIdMap.get(dependency.getDest());
            adjList.get(source).add(new Pair<>(dest, dependency.getCommunicationTime()));
            revAdjList.get(dest).add(new Pair<>(source, dependency.getCommunicationTime()));
        }
    }

    private void initTopologicalOrderIndex() {
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
        if (topologicalOrder.size() != _numTasks) {
            throw new SchedulerException("This is not a DAG, hence no valid schedule exists.");
        }

        topologicalIndex = new ArrayList<>();
        for (int i = 0; i < _numTasks; i++) topologicalIndex.add(0);

        for (int i = 0; i < _numTasks; i++) {
            topologicalIndex.set(topologicalOrder.get(i), i);
        }
    }

    private void initTaskWeights() {
        taskWeights = new ArrayList<>();
        for (int i = 0; i < _numTasks; i++) {
            taskWeights.add(_taskGraph.getTasks().get(i).getTaskTime());
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
