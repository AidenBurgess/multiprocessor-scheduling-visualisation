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

    // task graph object with the name, tasks and dependencies of the graph.
    private final TaskGraph _taskGraph;

    // total number of tasks
    private int _numTasks;

    // adjacency list which shows all node's children and
    // adjacency list which shows all the children's parents
    private ArrayList<ArrayList<Pair<Integer, Integer>>> _adjList, _revAdjList;

    // A list containing the topological order, the index of the topological order and the tasks weights
    private ArrayList<Integer> _topologicalOrder, _topologicalIndex, _taskWeights;

    // map for the task names to their id in the application.
    private HashMap<String, Integer> _taskNameToIdMap;

    public DataStructures(TaskGraph taskGraph) {
        _taskGraph = taskGraph;

        checkValidWeights();

        initIdMapping();
        initAdjLists();
        initTopologicalOrderIndex();
        initTaskWeights();
    }

    /**
     * Initialises the tasks to an id.
     */
    private void initIdMapping() {
        _numTasks = _taskGraph.getTasks().size();

        // Mapping each Task object to an integer id. 0-indexed.
        _taskNameToIdMap = new HashMap<>();
        for (int i = 0; i < _taskGraph.getTasks().size(); i++) {
            _taskNameToIdMap.put(_taskGraph.getTasks().get(i).getName(), i);
        }
    }

    /**
     * Checks if the task weights are all positive, and the dependency communication times
     * are all non-negative.
     */
    private void checkValidWeights() {
        for (Task task : _taskGraph.getTasks()) {
            if (task.getTaskTime() <= 0) {
                throw new SchedulerException("The task weight must be positive.");
            }
        }
        for (Dependency dependency : _taskGraph.getDependencies()) {
            if (dependency.getCommunicationTime() < 0) {
                throw new SchedulerException("The dependency communication time must be non-negative.");
            }
        }
    }

    /**
     * Initalises the adjacency lists for the tasks from the dependencies.
     */
    private void initAdjLists() {
        // Instantiating the adjacency list and reverse adjacency list.
        _adjList = new ArrayList<>(_numTasks);
        _revAdjList = new ArrayList<>(_numTasks);

        for (int i = 0; i < _numTasks; i++) {
            _adjList.add(new ArrayList<>());
            _revAdjList.add(new ArrayList<>());
        }
        // Substantiate the adjList and revAdjList.
        // The adjLists use the name -> id mapping from taskNameToIdMap
        ArrayList<Dependency> dependencies = _taskGraph.getDependencies();
        int dependenciesSize = dependencies.size();
        for (int i = 0; i < dependenciesSize; i++) {
            Dependency dependency = dependencies.get(i);

            int source = _taskNameToIdMap.get(dependency.getSource());
            int dest = _taskNameToIdMap.get(dependency.getDest());

            // add the nodes to the adjacency lists.
            _adjList.get(source).add(new Pair<>(dest, dependency.getCommunicationTime()));
            _revAdjList.get(dest).add(new Pair<>(source, dependency.getCommunicationTime()));
        }
    }

    /**
     * Initalises the topological order of the entire graph.
     */
    private void initTopologicalOrderIndex() {
        // Topological Ordering. The below code finds one valid topological ordering.
        // This also ensures that the TaskGraph input has no cyclic dependencies.
        _topologicalOrder = new ArrayList<>();

        // Queue all tasks that have no initial dependencies, and track the inDegree
        boolean[] visited = new boolean[_numTasks];
        int[] inDegree = new int[_numTasks];
        LinkedList<Integer> queue = new LinkedList<>();

        // initialise the queue for all the 0 in degree tasks.
        for (int i = 0; i < _numTasks; i++) {
            inDegree[i] = _revAdjList.get(i).size();
            if (inDegree[i] == 0) {
                _topologicalOrder.add(i);
                queue.push(i);
                visited[i] = true;
            }
        }

        // Take off tasks one by one, and update the inDegree
        while (!queue.isEmpty()) {
            int task = queue.poll();

            ArrayList<Pair<Integer, Integer>> revTask = _adjList.get(task);
            int numRevTasks = revTask.size();

            for (int i = 0; i < numRevTasks; i++) {

                Pair<Integer, Integer> dependency = revTask.get(i);
                int child = dependency.getKey();
                inDegree[child]--;

                // if the child now has an in degree of 0 and hasn't been visited, then add to queue
                if (inDegree[child] == 0 && !visited[child]) {
                    _topologicalOrder.add(child);
                    visited[child] = true;
                    queue.push(child);
                }
            }
        }

        // If ind != n, there are some tasks that have dependencies on each other.
        if (_topologicalOrder.size() != _numTasks) {
            throw new SchedulerException("This is not a DAG, hence no valid schedule exists.");
        }

        _topologicalIndex = new ArrayList<>();
        for (int i = 0; i < _numTasks; i++) {
            _topologicalIndex.add(0);
        }

        // set the id of the task to the corresponding topological order.
        for (int i = 0; i < _numTasks; i++) {
            _topologicalIndex.set(_topologicalOrder.get(i), i);
        }
    }

    /**
     * Initialises the task weights.
     */
    private void initTaskWeights() {

        _taskWeights = new ArrayList<>();
        for (int i = 0; i < _numTasks; i++) {
            _taskWeights.add(_taskGraph.getTasks().get(i).getTaskTime());
        }
    }

    // ------------------ Getters and Setters ------------------- //
    public int getNumTasks() {
        return _numTasks;
    }

    public ArrayList<ArrayList<Pair<Integer, Integer>>> getAdjList() {
        return _adjList;
    }

    public ArrayList<ArrayList<Pair<Integer, Integer>>> getRevAdjList() {
        return _revAdjList;
    }

    public ArrayList<Integer> getTopologicalOrder() {
        return _topologicalOrder;
    }

    public ArrayList<Integer> getTopologicalIndex() {
        return _topologicalIndex;
    }

    public ArrayList<Integer> getTaskWeights() {
        return _taskWeights;
    }
}
