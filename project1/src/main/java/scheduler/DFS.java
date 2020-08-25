package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * The DFS class is responsible for executing the DFS.
 * The class holds a state, and on every 'branch', it adjusts the state in-place.
 * -    In-place adjustment = O(1), much better than making a new copy of State
 *
 * The class has a copy of the revAdjList and taskTimes from the VariableScheduler to provide
 * context to determine the next possible moves.
 */

public abstract class DFS {
    private int _numTasks;
    protected State _state;
    protected Bound _bound;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> _adjList, _revAdjList;
    private ArrayList<Integer> _taskTimes;
    protected InformationHolder _informationHolder;

    public DFS(State state, Bound bound,
               ArrayList<ArrayList<Pair<Integer, Integer>>> adjList,
               ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList,
               ArrayList<Integer> taskTimes,
               InformationHolder informationHolder) {
        _state = state;
        _bound = bound;
        _adjList = adjList;
        _revAdjList = revAdjList;
        _taskTimes = taskTimes;
        _numTasks = _state._numTasks;
        _informationHolder = informationHolder;
    }

    /**
     * Performs DFS.
     *
     * If the current State is complete, it will update
     * -    bound,
     * -    bestProcessorMap,
     * -    bestStartTimeMap
     * in the ReducedStateScheduler.
     *
     * If the current state is not complete, it will try place each task on each processor, if legal.
     * The placement is legal if:
     * -    All the task's dependencies have been met
     * -    The task is not already assigned
     * On a successful placement, the current state is updated and run() is called. When run() finishes
     * executing, the state is restored.
     *
     * If at any stage, the current state's endTime exceeds the bound, DFS will "prune" and the current
     * run() will return.
     */
    protected final void run() {
        run(-1, -1);
    }

    private final void run(int prevTask, int prevProcessor) { // try this for now
        onDFSEntry();

        // Prune
        if (_bound.canPrune(_state._endTime)) {
            onDFSExit();
            return;
        }

        // If current state is complete
        if (_state._unassignedTasks == 0) {
            _bound.reduceBound(_state._endTime);

            onCompleteSchedule();
            onDFSExit();
            return;
        }

        // For each task,
        for (int task = 0; task < _numTasks; task++) {
            // If the task is scheduled, ignore
            if (_state._assignedProcessorId[task] != State.UNSCHEDULED) continue;

            // If the task still has unscheduled dependencies, ignore
            boolean dependenciesMet = true;
            boolean isPrevTasksChild = false;

            ArrayList<Pair<Integer, Integer>> revTask = _revAdjList.get(task);
            int numRevTasks = revTask.size();
            for (int i = 0; i < numRevTasks; i++) {
                Pair<Integer, Integer> dependency = revTask.get(i);
                int parent = dependency.getKey();
                if (_state._assignedProcessorId[parent] == State.UNSCHEDULED) {
                    dependenciesMet = false;
                    break;
                }
                if (parent == prevTask) isPrevTasksChild = true;
            }
            if (!dependenciesMet) continue;

            // For each processor,
            for (int processor = 0; processor < _state._numProcessors; processor++) {
                // Prune
                if (_bound.canPrune(_state._endTime)) {
                    onDFSExit();
                    return;
                }

                // Duplicate state.... ?
                if (processor < prevProcessor && !isPrevTasksChild) continue;

                // Find the earliest time that task can be placed on processor.
                // For each of its dependencies, make sure that there is enough delay.
                int nextTaskStartTime = _state._processorEndTime[processor];

                revTask = _revAdjList.get(task);
                numRevTasks = revTask.size();
                for (int i = 0; i < numRevTasks; i++) {
                    Pair<Integer, Integer> dependency = revTask.get(i);
                    int parent = dependency.getKey();
                    int delay = dependency.getValue();

                    if (_state._assignedProcessorId[parent] == processor) continue;

                    // ensures that the start time is at least the parent's end time + delay IF the parent is on a different processor
                    nextTaskStartTime = Math.max(nextTaskStartTime, _state._taskEndTime[parent] + delay);
                }

                // Save the current state
                int nextTaskEndTime = nextTaskStartTime + _taskTimes.get(task);
                int processorPrevEndTime = _state._processorEndTime[processor];
                int prevEndTime = _state._endTime;

                // Update current state
                _state._taskEndTime[task] = nextTaskEndTime;
                _state._assignedProcessorId[task] = processor;
                _state._taskInDegree[task]--;
                _state._processorEndTime[processor] = nextTaskEndTime;
                _state._unassignedTasks--;
                _state._endTime = Math.max(_state._endTime, nextTaskEndTime);

                // Recursive call
                run(task, processor);

                // Restore current state
                _state._taskEndTime[task] = State.UNSCHEDULED;
                _state._assignedProcessorId[task] = State.UNSCHEDULED;
                _state._taskInDegree[task]++;
                _state._processorEndTime[processor] = processorPrevEndTime;
                _state._unassignedTasks++;
                _state._endTime = prevEndTime;
            }
        }

        onDFSExit();
    }

    /**
     * Hook method that is called on every run() entry
     * Responsible for updating information in the InformationHolder, if necessary
     */
    protected abstract void onDFSEntry();

    /**
     * Hook method that is called on every run() exit
     * Responsible for updating information in the InformationHolder, if necessary
     */
    protected abstract void onDFSExit();

    /**
     * Hook method that is called on every new complete schedule that is found
     * Responsible for updating information in the InformationHolder, if necessary
     */
    protected abstract void onCompleteSchedule();
}
