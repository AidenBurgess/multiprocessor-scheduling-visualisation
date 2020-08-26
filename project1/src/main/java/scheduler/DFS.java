package main.java.scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * The DFS class is responsible for executing the DFS.
 * The class holds a state, and on every 'branch', it adjusts the state in-place.
 * -    In-place adjustment = O(1), much better than making a new copy of State
 *
 * On its changes, it will alert the DFSListener object.
 */

public class DFS {
    private int _numTasks;
    protected State _state;
    protected Bound _bound;
    protected DataStructures _dataStructures;
    protected DFSListener _dfsListener;

    public DFS(State state, Bound bound, DataStructures dataStructures, DFSListener dfsListener) {
        _state = state;
        _bound = bound;
        _dataStructures = dataStructures;
        _numTasks = _state._numTasks;
        _dfsListener = dfsListener;
    }

    /**
     * Performs DFS.
     *
     * On:
     * - enter DFS
     * - exit DFS
     * - complete schedule
     * - partial schedule
     * the DFSListener will be alerted.
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
    protected void run(int prevTask, int prevProcessor) {
        _dfsListener.onDFSEntry();
        _dfsListener.onPartialSchedule(_state, _bound);

        // Prune
        if (_bound.canPrune(FFunction.evaluate(_state))) {
            _dfsListener.onDFSExit();
            return;
        }

        // If current state is complete
        if (_state._unassignedTasks == 0) {
            _bound.reduceBound(_state._endTime);

            _dfsListener.onCompleteSchedule(_state, _bound);
            _dfsListener.onDFSExit();
            return;
        }

        // For each task,
        for (int task = 0; task < _numTasks; task++) {
            // If the task is scheduled, ignore
            if (_state._assignedProcessorId[task] != State.UNSCHEDULED) continue;

            // If the task still has unscheduled dependencies, ignore
            boolean dependenciesMet = true;

            // Determine if the task is the previously placed task's child
            boolean isPrevTasksChild = false;

            // get all the parent tasks from the child
            ArrayList<Pair<Integer, Integer>> parentTasks = _dataStructures.getRevAdjList().get(task);
            int numParentTasks = parentTasks.size();
            for (int i = 0; i < numParentTasks; i++) {
                Pair<Integer, Integer> dependency = parentTasks.get(i);
                int parent = dependency.getKey();

                // if the current task is the previous tasks child
                if (parent == prevTask) isPrevTasksChild = true;

                // if unscheduled, then we can schedule
                if (_state._taskEndTime[parent] == State.UNSCHEDULED) {
                    dependenciesMet = false;
                    break;
                }
            }

            if (!dependenciesMet) {
                continue;
            }

            // For each processor,
            for (int processor = 0; processor < _state._numProcessors; processor++) {

                // Prune
                if (_bound.canPrune(FFunction.evaluate(_state))) {
                    _dfsListener.onDFSExit();
                    return;
                }

                // The task is only allowed to be placed in an earlier processor
                // if it was a child of the parent
                if (processor < prevProcessor && !isPrevTasksChild) {
                    continue;
                }

                // You can only put a task on an empty processor if the currentTask is larger than
                // the firstTask of the previous processor
                // todo check if tasks are allowed to be weight 0
                if (_state._processorEndTime[processor] == 0) {

                    // if this is not the freeProcessor
                    if (processor != _state._freeProcessor) {
                        break;
                    }
                    if (processor != 0) {
                        int curTopologicalIndex = _dataStructures.getTopologicalIndex().get(task);
                        int prevTopologicalIndex = _dataStructures.getTopologicalIndex().get(_state._prevProcessorFirstTask);

                        if (curTopologicalIndex < prevTopologicalIndex) {
                            continue;
                        }
                    }
                }

                // Find the earliest time that task can be placed on processor.
                // For each of its dependencies, make sure that there is enough delay.
                int nextTaskStartTime = _state._processorEndTime[processor];

                parentTasks = _dataStructures.getRevAdjList().get(task);
                numParentTasks = parentTasks.size();
                for (int i = 0; i < numParentTasks; i++) {
                    Pair<Integer, Integer> dependency = parentTasks.get(i);
                    int parent = dependency.getKey();
                    int delay = dependency.getValue();

                    if (_state._assignedProcessorId[parent] == processor) continue;

                    // ensures that the start time is at least the parent's end time + delay IF the parent is on a different processor
                    nextTaskStartTime = Math.max(nextTaskStartTime, _state._taskEndTime[parent] + delay);
                }

                // Save the current state
                int nextTaskEndTime = nextTaskStartTime + _dataStructures.getTaskWeights().get(task);
                int processorPrevEndTime = _state._processorEndTime[processor];
                int prevEndTime = _state._endTime;
                int prevFreeProcessor = _state._freeProcessor;
                int prevPrevProcessorFirstTask = _state._prevProcessorFirstTask;

                // Update current state
                _state._taskEndTime[task] = nextTaskEndTime;
                _state._assignedProcessorId[task] = processor;
                _state._processorEndTime[processor] = nextTaskEndTime;
                _state._unassignedTasks--;
                _state._endTime = Math.max(_state._endTime, nextTaskEndTime);
                _state._computationalTime += (nextTaskEndTime - prevEndTime) - _dataStructures.getTaskWeights().get(task);

                // if there this processor is a free processor, increase the free processor
                if (processor == _state._freeProcessor) {
                    _state._freeProcessor++;
                    _state._prevProcessorFirstTask = task;
                }

                // Recursive call
                run(task, processor);

                // Restore current state
                _state._taskEndTime[task] = State.UNSCHEDULED;
                _state._assignedProcessorId[task] = State.UNSCHEDULED;
                _state._processorEndTime[processor] = processorPrevEndTime;
                _state._unassignedTasks++;
                _state._endTime = prevEndTime;
                _state._freeProcessor = prevFreeProcessor;
                _state._prevProcessorFirstTask = prevPrevProcessorFirstTask;
                _state._computationalTime -= (nextTaskEndTime - prevEndTime) - _dataStructures.getTaskWeights().get(task);
            }
        }

        _dfsListener.onDFSExit();
    }

    /**
     * First external run() call
     */
    public void run() {
        run(-1, -1);
    }

    protected void waitForFinish() {

    }
}
