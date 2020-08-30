package main.java.scheduler;

import main.java.commandparser.Config;
import main.java.dotio.TaskGraph;
import main.java.exception.SchedulerException;

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

    // Holds the data structures used throughout the Scheduling, such as adjacency lists.
    private DataStructures _dataStructures;

    // DFS instance which will run the dfs on a state
    private DFS _dfs;

    // Listener to the DFS Instance
    private DFSListener _dfsListener;

    // Holds the middle-man information. The DFS instances can pass data into this class for external classes
    // to access.
    private final InformationHolder _informationHolder;

    private VariableScheduler(TaskGraph taskGraph, int numProcessors) {
        // There must be >= 1 processor for there to be a schedule
        if (numProcessors <= 0) {
            throw new SchedulerException("The number of processors must be positive.");
        }
        _informationHolder = new InformationHolder(taskGraph);
        _numTasks = taskGraph.getTasks().size();
        _numProcessors = numProcessors;
        _taskGraph = taskGraph;
        _dataStructures = new DataStructures(taskGraph);
    }

    /**
     * Overriden constructor if statistics are required and the number of cores
     * @param taskGraph
     * @param numProcessors
     * @param recordStatistics
     * @param numParallelCores
     */
    public VariableScheduler(TaskGraph taskGraph, int numProcessors, boolean recordStatistics, int numParallelCores) {
        this(taskGraph, numProcessors);

        State startingState = new State(_numTasks, _numProcessors, _dataStructures);

        // Determine which implementations will be used when performing DFS.
        // Record/not record statistics
        _dfsListener = recordStatistics
                ? new StatisticDFSListener(_informationHolder)
                : new MinimalDFSListener(_informationHolder);
        // Parallel/non parallel implementations
        _dfs = numParallelCores == Config.SEQUENTIAL_EXECUTION
                ? new DFS(startingState, _bound, _dataStructures, _dfsListener)
                : new RecursiveParallelDFS(startingState, _bound, _dataStructures, _dfsListener);

        // Initialise static thread pool
        if (numParallelCores != Config.SEQUENTIAL_EXECUTION) {
            ParallelDFS.initialiseThreadPool(numParallelCores);
        }
    }

    /**
     * executor method which will set the status of the information holder
     * and then runs dfs.
     */
    @Override
    public void execute() {
        _informationHolder.setSchedulerStatus(InformationHolder.RUNNING);

        _dfs.run();
        _dfs.waitForFinish();

        _informationHolder.setSchedulerStatus(InformationHolder.FINISHED);
    }

    /**
     * Getter for the information holder.
     * @return InformationHolder the information holder
     */
    @Override
    public InformationHolder getInformationHolder() {
        return _informationHolder;
    }
}

