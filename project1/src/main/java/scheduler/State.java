package main.java.scheduler;

/**
 * The State class holds information that defines an allocation of tasks on processors.
 */
public class State {
    protected static final int UNSCHEDULED = -1;
    protected final int _numTasks, _numProcessors;
    protected int _computationalTime;

    protected int[] _assignedProcessorId; // assignedProcessorId[taskId] -> processorId
    protected int[] _taskEndTime; // taskEndTime[taskId] -> end time of task
    protected int[] _taskInDegree; // taskInDegree[taskId] -> how many dependencies task is waiting on
    protected int[] _processorEndTime; // processorEndTime[processorId] -> end time of processor

    protected int _endTime; // end time of the last processor
    protected int _unassignedTasks; // number of tasks still unassigned
    protected int _freeProcessor, _prevProcessorFirstTask;

    private State(int numTasks, int numProcessors) {
        _numTasks = numTasks;
        _numProcessors = numProcessors;

        _unassignedTasks = numTasks; // initially n tasks are unassigned
        _endTime = 0;
        _freeProcessor = 0;
        _prevProcessorFirstTask = UNSCHEDULED;

        _processorEndTime = new int[numProcessors];
        _assignedProcessorId = new int[numTasks];
        _taskEndTime = new int[numTasks];
        _taskInDegree = new int[numTasks];

        for (int i = 0; i < numTasks; i++) {
            _taskEndTime[i] = UNSCHEDULED;
            _assignedProcessorId[i] = UNSCHEDULED;
        }

        for (int i = 0; i < numProcessors; i++) {
            _processorEndTime[i] = 0;
        }
    }

    public State(int numTasks, int numProcessors, DataStructures dataStructures) {
        this(numTasks, numProcessors);
        int taskWeight = 0;
        for (int i = 0; i < numTasks; i++) {
            taskWeight += dataStructures.getTaskWeights().get(i);
            _taskInDegree[i] = dataStructures.getRevAdjList().get(i).size();
        }

        _computationalTime = taskWeight;
    }

    /**
     * Returns a deep copy of the current State
     *
     * @return a new State instance with the same values.
     */
    public State copy() {
        State next = new State(_numTasks, _numProcessors);
        for (int i = 0; i < _numTasks; i++) {
            next._assignedProcessorId[i] = _assignedProcessorId[i];
            next._taskEndTime[i] = _taskEndTime[i];
        }
        for (int i = 0; i < _numProcessors; i++) {
            next._processorEndTime[i] = _processorEndTime[i];
        }

        next._endTime = _endTime;
        next._unassignedTasks = _unassignedTasks;
        return next;
    }
}
