package main.java.scheduler;

import javafx.util.Pair;
import main.java.dotio.Task;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The DFS class is responsible for executing the DFS.
 * The class holds the State, and on every 'branch', it adjusts State in-place.
 * -    In-place adjustment = O(1), much better than making a new copy of State
 * <p>
 * This class is nested in ReducedStateScheduler as it requires access to fields such as bound, and is
 * only instantiated within a ReducedStateScheduler context.
 */

public abstract class DFS {
    int numTasks;
    State state;
    Bound bound;
    ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList;
    ArrayList<Integer> taskTimes;
    InformationHolder informationHolder;

    public DFS(State state, Bound bound, ArrayList<ArrayList<Pair<Integer, Integer>>> revAdjList, ArrayList<Integer> taskTimes, InformationHolder informationHolder) {
        this.state = state;
        this.bound = bound;
        this.revAdjList = revAdjList;
        this.taskTimes = taskTimes;
        this.numTasks = state.numTasks;
        this.informationHolder = informationHolder;
    }

    /**
     * Performs DFS.
     * <p>
     * If the current State is complete, it will update
     * -    bound,
     * -    bestProcessorMap,
     * -    bestStartTimeMap
     * in the ReducedStateScheduler.
     * <p>
     * If the current state is not complete, it will try place each task on each processor, if legal.
     * The placement is legal if:
     * -    All the task's dependencies have been met
     * -    The task is not already assigned
     * On a successful placement, the current state is updated and run() is called. When run() finishes
     * executing, the state is restored.
     * <p>
     * If at any stage, the current state's endTime exceeds the bound, DFS will "prune" and the current
     * run() will return.
     */
    protected final void run() {
        onDFSEntry();

        // Prune
        if (bound.canPrune(state.endTime)) {
            onDFSExit();
            return;
        }

        // If current state is complete
        if (state.unassignedTasks == 0) {
            bound.reduceBound(state.endTime);

            onCompleteSchedule();
            onDFSExit();
            return;
        }

        // For each task,
        for (int task = 0; task < numTasks; task++) {
            // If the task is scheduled, ignore
            if (state.assignedProcessorId[task] != State.UNSCHEDULED) continue;

            // If the task still has unscheduled dependencies, ignore
            boolean dependenciesMet = true;

            ArrayList<Pair<Integer, Integer>> revTask = revAdjList.get(task);
            int numRevTasks = revTask.size();
            for (int i = 0; i < numRevTasks; i++) {
                Pair<Integer, Integer> dependency = revTask.get(i);
                int parent = dependency.getKey();
                if (state.assignedProcessorId[parent] == State.UNSCHEDULED) {
                    dependenciesMet = false;
                    break;
                }
            }
            if (!dependenciesMet) continue;

            // For each processor,
            for (int processor = 0; processor < state.numProcessors; processor++) {
                // Prune
                if (bound.canPrune(state.endTime)) {
                    onDFSExit();
                    return;
                }

                // Find the earliest time that task can be placed on processor.
                // For each of its dependencies, make sure that there is enough delay.
                int nextTaskStartTime = state.processorEndTime[processor];

                revTask = revAdjList.get(task);
                numRevTasks = revTask.size();
                for (int i = 0; i < numRevTasks; i++) {
                    Pair<Integer, Integer> dependency = revTask.get(i);
                    int parent = dependency.getKey();
                    int delay = dependency.getValue();

                    if (state.assignedProcessorId[parent] == processor) continue;

                    // ensures that the start time is at least the parent's end time + delay IF the parent is on a different processor
                    nextTaskStartTime = Math.max(nextTaskStartTime, state.taskEndTime[parent] + delay);
                }

                // Save the current state
                int nextTaskEndTime = nextTaskStartTime + taskTimes.get(task);
                int processorPrevEndTime = state.processorEndTime[processor];
                int prevEndTime = state.endTime;

                // Update current state
                state.taskEndTime[task] = nextTaskEndTime;
                state.assignedProcessorId[task] = processor;
                state.processorEndTime[processor] = nextTaskEndTime;
                state.unassignedTasks--;
                state.endTime = Math.max(state.endTime, nextTaskEndTime);

                // Recursive call
                run();

                // Restore current state
                state.taskEndTime[task] = State.UNSCHEDULED;
                state.assignedProcessorId[task] = State.UNSCHEDULED;
                state.processorEndTime[processor] = processorPrevEndTime;
                state.unassignedTasks++;
                state.endTime = prevEndTime;
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
