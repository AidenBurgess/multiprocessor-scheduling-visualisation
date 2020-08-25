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

    private ArrayList<ArrayList<Pair<Integer,Integer>>> _adjList, _revAdjList;
    private ArrayList<Integer> _taskTimes;

    private DFSExecutor _dfsExecutor;
    private StatisticToggle _statisticToggle;
    private final InformationHolder _informationHolder;

    private VariableScheduler(TaskGraph taskGraph, int numProcessors) {
        _informationHolder = new InformationHolder(taskGraph);
        _numTasks = taskGraph.getTasks().size();
        _taskTimes = new ArrayList<>();
        _numProcessors = numProcessors;
        _taskGraph = taskGraph;


        initializeDataStructures();
    }

    public VariableScheduler(TaskGraph taskGraph, int numProcessors, boolean recordStatistics, int numParallelCores) {
        this(taskGraph, numProcessors);

        // Determine which implementations
        _statisticToggle = recordStatistics ? new YesStatisticToggle() : new NoStatisticToggle();
        _dfsExecutor = numParallelCores == Config.SEQUENTIAL_EXECUTION ? new NormalDFSExecutor() : new ParallelDFSExecutor(numParallelCores);
    }

    private void initializeDataStructures() {
        // Mapping each Task object to an integer id. 0-indexed.
        HashMap<String, Integer> taskNameToIdMap = new HashMap<>();
        for (int i = 0; i < _taskGraph.getTasks().size(); i++) {
            taskNameToIdMap.put(_taskGraph.getTasks().get(i).getName(), i);
        }

        // Instantiating taskTimes
        for (int i = 0; i < _taskGraph.getTasks().size(); i++) {
            _taskTimes.add(_taskGraph.getTasks().get(i).getTaskTime());
        }

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
            int source = taskNameToIdMap.get(dependency.getSource());
            int dest = taskNameToIdMap.get(dependency.getDest());
            _adjList.get(source).add(new Pair<>(dest, dependency.getCommunicationTime()));
            _revAdjList.get(dest).add(new Pair<>(source, dependency.getCommunicationTime()));
        }
    }

    @Override
    public void execute() {
        _informationHolder.setSchedulerStatus(InformationHolder.RUNNING);
        State state = new State(_numTasks, _numProcessors, _revAdjList);

        _dfsExecutor.runDFS(_statisticToggle.getDFS(state, _bound, _adjList, _revAdjList, _taskTimes, _informationHolder));

        _dfsExecutor.waitForFinish(); // This is called to let the executor clean up
        _informationHolder.setSchedulerStatus(InformationHolder.FINISHED);
    }

    @Override
    public InformationHolder getInformationHolder() {
        return _informationHolder;
    }
}

