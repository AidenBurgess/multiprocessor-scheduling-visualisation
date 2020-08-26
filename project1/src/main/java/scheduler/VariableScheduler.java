package main.java.scheduler;

import javafx.util.Pair;
import main.java.commandparser.Config;
import main.java.dotio.Dependency;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * An implementation of Scheduler that uses DFS Branch and Bound.
 *
 * The Branch and Bound technique is a brute force strategy. At any 'State', it tries to place any free task onto
 * any processor. The 'Bound' aspect is that when some complete solution is found with endTime = X, any branch that
 * exceeds X is "pruned", reducing the search space drastically.
 *
 * The implementation reduces memory as it has these properties:
 * -    No Strings - on construction the Scheduler maps each name to an int.
 * -    No HashMaps - as there are no Strings, arrays can be used. e.g. array[id] instead of map.get(name)
 * -    Less Classes - There is no Processor or TaskNode class, favouring performance over OOP.
 *
 */
public class VariableScheduler implements Scheduler {
    private final int _numTasks, _numProcessors;
    private final Bound _bound = new Bound();
    private final TaskGraph _taskGraph;

    private DataStructures _dataStructures;

    private DFS _dfs;
    private DFSListener _dfsListener;
    private final InformationHolder _informationHolder;

    private VariableScheduler(TaskGraph taskGraph, int numProcessors) {
        _informationHolder = new InformationHolder(taskGraph);
        _numTasks = taskGraph.getTasks().size();
        _numProcessors = numProcessors;
        _taskGraph = taskGraph;

        initializeDataStructures();
    }

    public VariableScheduler(TaskGraph taskGraph, int numProcessors, boolean recordStatistics, int numParallelCores) {
        this(taskGraph, numProcessors);

        State startingState = new State(_numTasks, _numProcessors, _dataStructures);

        // Determine which implementations
        _dfsListener = recordStatistics
                ? new StatisticDFSListener(_informationHolder)
                : new MinimalDFSListener(_informationHolder);
        _dfs = numParallelCores == Config.SEQUENTIAL_EXECUTION
                ? new DFS(startingState, _bound, _dataStructures, _dfsListener)
                : new ParallelDFS(startingState, _bound, _dataStructures, _dfsListener);

        // Initialise static thread pool
        if (numParallelCores != Config.SEQUENTIAL_EXECUTION) {
            ParallelDFS.initialiseThreadPool(numParallelCores);
        }
    }

    private void initializeDataStructures() {
        _dataStructures = new DataStructures();

        // Mapping each Task object to an integer id. 0-indexed.
        HashMap<String, Integer> taskNameToIdMap = new HashMap<>();
        for (int i = 0; i < _taskGraph.getTasks().size(); i++) {
            taskNameToIdMap.put(_taskGraph.getTasks().get(i).getName(), i);
        }

        ArrayList<ArrayList<Pair<Integer, Integer>>> adjList, revAdjList;

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
            int source = taskNameToIdMap.get(dependency.getSource());
            int dest = taskNameToIdMap.get(dependency.getDest());
            adjList.get(source).add(new Pair<>(dest, dependency.getCommunicationTime()));
            revAdjList.get(dest).add(new Pair<>(source, dependency.getCommunicationTime()));
        }

        // Topological Ordering. The below code finds one valid topological ordering.
        // This also ensures that the TaskGraph input has no cyclic dependencies.
        ArrayList<Integer> topologicalOrder = new ArrayList<>();

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

        ArrayList<Integer> topologicalIndex = new ArrayList<>();
        for (int i = 0; i < _numTasks; i++) topologicalIndex.add(0);

        for (int i = 0; i < _numTasks; i++) {
            topologicalIndex.set(topologicalOrder.get(i), i);
        }

        ArrayList<Integer> taskWeights = new ArrayList<>();
        for (int i = 0; i < _numTasks; i++) {
            taskWeights.add(_taskGraph.getTasks().get(i).getTaskTime());
        }

        _dataStructures.setTopologicalIndex(topologicalIndex);
        _dataStructures.setAdjList(adjList);
        _dataStructures.setRevAdjList(revAdjList);
        _dataStructures.setTaskWeights(taskWeights);
    }

    @Override
    public void execute() {
        _informationHolder.setSchedulerStatus(InformationHolder.RUNNING);

        _dfs.run();
        _dfs.waitForFinish();

        _informationHolder.setSchedulerStatus(InformationHolder.FINISHED);
    }

    @Override
    public InformationHolder getInformationHolder() {
        return _informationHolder;
    }
}

