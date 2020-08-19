package main.java.scheduler;

import javafx.util.Pair;
import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;

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
public class ReducedStateScheduler implements Scheduler {
    public static final int NO_SOLUTION = -1;

    int n, p;
    int bound = NO_SOLUTION; // The current best solution.
    TaskGraph input;

    ArrayList<ArrayList<Pair<Integer,Integer>>> adjList, revAdjList;

    HashMap<String, Integer> bestStartTimeMap, bestProcessorMap;


    public ReducedStateScheduler(TaskGraph taskGraph, int processors) {
        n = taskGraph.getTasks().size();
        p = processors;
        input = taskGraph;

        // Mapping each Task object to an integer id. 0-indexed.
        HashMap<String, Integer> taskNameToIdMap = new HashMap<>();
        for (int i = 0; i < taskGraph.getTasks().size(); i++) {
            taskNameToIdMap.put(taskGraph.getTasks().get(i).getName(), i);
        }

        // Instantiating the adjacency list and reverse adjacency list.
        adjList = new ArrayList<>(n);
        revAdjList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
            revAdjList.add(new ArrayList<>());
        }

        // Substantiate the adjList and revAdjList.
        // The adjLists use the name -> id mapping from taskNameToIdMap
        for (Dependency dependency : taskGraph.getDependencies()) {
            int source = taskNameToIdMap.get(dependency.getSource());
            int dest = taskNameToIdMap.get(dependency.getDest());
            adjList.get(source).add(new Pair<>(dest, dependency.getCommunicationTime()));
            revAdjList.get(dest).add(new Pair<>(source, dependency.getCommunicationTime()));
        }
    }

    @Override
    public void execute() {
        // On execute, the Scheduler creates a new, empty state and runs DFS.
        State state = new State(n, p);
        DFS dfs = new DFS(state);
        dfs.run();
    }

    @Override
    public HashMap<String, Integer> getCurrentStartTimeMap() {
        return null;
    }

    @Override
    public HashMap<String, Integer> getCurrentProcessorMap() {
        return null;
    }

    @Override
    public HashMap<String, Integer> getBestStartTimeMap() {
        return bestStartTimeMap;
    }

    @Override
    public HashMap<String, Integer> getBestProcessorMap() {
        return bestProcessorMap;
    }

    @Override
    public int getCurrentBound() {
        return bound;
    }

    @Override
    public long getTotalStatesVisited() {
        return 0;
    }

    @Override
    public long getCompleteStatesVisited() {
        return 0;
    }

    @Override
    public int getActiveBranches() {
        return 0;
    }

    /**
     * The DFS class is responsible for executing the DFS.
     * The class holds the State, and on every 'branch', it adjusts State in-place.
     * -    In-place adjustment = O(1), much better than making a new copy of State
     *
     * This class is nested in ReducedStateScheduler as it requires access to fields such as bound, and is
     * only instantiated within a ReducedStateScheduler context.
     */
    class DFS {
        State state;

        public DFS(State state) {
            this.state = state;
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
        private void run() {
            // Prune
            if (bound != NO_SOLUTION && state.endTime >= bound) return;

            // If current state is complete
            if (state.unassignedTasks == 0) {
                // Update startTime/processor maps
                bestStartTimeMap = new HashMap<>();
                bestProcessorMap = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    Task task = input.getTasks().get(i);
                    bestStartTimeMap.put(task.getName(), state.taskEndTime[i] - task.getTaskTime()); // end time - task time = start time
                    bestProcessorMap.put(task.getName(), state.assignedProcessorId[i] + 1); // 1-indexed
                }

                // Update bound
                if (bound == NO_SOLUTION) bound = state.endTime;
                else bound = Math.min(bound, state.endTime);
                return;
            }

            // For each task,
            for (int task = 0; task < n; task++) {
                // If the task is scheduled, ignore
                if (state.assignedProcessorId[task] != State.UNSCHEDULED) continue;

                // If the task still has unscheduled dependencies, ignore
                boolean dependenciesMet = true;
                for (Pair<Integer, Integer> dependency : revAdjList.get(task)) {
                    int parent = dependency.getKey();
                    if (state.assignedProcessorId[parent] == State.UNSCHEDULED) {
                        dependenciesMet = false;
                        break;
                    }
                }
                if (!dependenciesMet) continue;

                // For each processor,
                for (int processor = 0; processor < p; processor++) {
                    // Prune
                    if (bound != NO_SOLUTION && state.endTime >= bound) return;

                    // Find the earliest time that task can be placed on processor.
                    // For each of its dependencies, make sure that there is enough delay.
                    int nextTaskStartTime = state.processorEndTime[processor];
                    for (Pair<Integer, Integer> dependency : revAdjList.get(task)) {
                        int parent = dependency.getKey();
                        int delay = dependency.getValue();

                        if (state.assignedProcessorId[parent] == processor) continue;

                        // ensures that the start time is at least the parent's end time + delay IF the parent is on a different processor
                        nextTaskStartTime = Math.max(nextTaskStartTime, state.taskEndTime[parent] + delay);
                    }

                    // Save the current state
                    int nextTaskEndTime = nextTaskStartTime + input.getTasks().get(task).getTaskTime();
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
        }
    }
}

