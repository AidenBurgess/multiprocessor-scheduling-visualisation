package main.java.scheduler;

import main.java.commandparser.Config;
import main.java.dotio.TaskGraph;

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
        _dataStructures = new DataStructures(taskGraph);
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
                : new RecursiveParallelDFS(startingState, _bound, _dataStructures, _dfsListener);

        // Initialise static thread pool
        if (numParallelCores != Config.SEQUENTIAL_EXECUTION) {
            ParallelDFS.initialiseThreadPool(numParallelCores);
        }
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

