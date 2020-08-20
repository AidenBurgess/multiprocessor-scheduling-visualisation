package main.java.scheduler;

import javafx.util.Pair;
import main.java.dotio.Dependency;
import main.java.dotio.Task;
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
    public static final int NO_SOLUTION = -1;

    private int numTasks, numProcessors;
    protected Bound bound = new Bound();
    private TaskGraph input;

    private int[] topologicalOrder;
    private ArrayList<ArrayList<Pair<Integer,Integer>>> adjList, revAdjList;
    private ArrayList<Integer> taskTimes = new ArrayList<>();

    private DFSExecutor dfsExecutor = new NormalDFSExecutor();
    private StatisticToggle statisticToggle = new NoStatisticToggle();
    InformationHolder informationHolder;

    public VariableScheduler(TaskGraph taskGraph, int processors, boolean recordStatistics, boolean isParallelised) {
        informationHolder = new InformationHolder(taskGraph);
        numTasks = taskGraph.getTasks().size();
        numProcessors = processors;
        input = taskGraph;

        // Mapping each Task object to an integer id. 0-indexed.
        HashMap<String, Integer> taskNameToIdMap = new HashMap<>();
        for (int i = 0; i < taskGraph.getTasks().size(); i++) {
            taskNameToIdMap.put(taskGraph.getTasks().get(i).getName(), i);
        }

        // Instantiating taskTimes
        for (int i = 0; i < taskGraph.getTasks().size(); i++) {
            taskTimes.add(taskGraph.getTasks().get(i).getTaskTime());
        }

        // Instantiating the adjacency list and reverse adjacency list.
        adjList = new ArrayList<>(numTasks);
        revAdjList = new ArrayList<>(numTasks);
        for (int i = 0; i < numTasks; i++) {
            adjList.add(new ArrayList<>());
            revAdjList.add(new ArrayList<>());
        }

        // Substantiate the adjList and revAdjList.
        // The adjLists use the name -> id mapping from taskNameToIdMap
        ArrayList<Dependency> dependencies = taskGraph.getDependencies();
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
        topologicalOrder = new int[numTasks];
        int ind = 0;

        // Queue all tasks that have no initial dependencies, and track the inDegree
        boolean[] visited = new boolean[numTasks];
        int[] inDegree = new int[numTasks];
        LinkedList<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numTasks; i++) {
            inDegree[i] = revAdjList.get(i).size();
            if (inDegree[i] == 0) {
                topologicalOrder[ind++] = i;
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
                    topologicalOrder[ind++] = child;
                    visited[child] = true;
                    queue.push(child);
                }
            }
        }

        // If ind != n, there are some tasks that have dependencies on each other.
        if (ind != numTasks) throw new RuntimeException("No topological ordering found"); // todo what we want to happen?
    }

    @Override
    public void execute() {
        dfsCaller(0, 0);
    }

    /**
     * The problem is that placing a task on an empty processor is the same as placing it on another
     * empty processor.
     *
     * To overcome this - we choose some subset of tasks as our "starts" (where each one is on an empty processor),
     * then all the other tasks must go on top of these tasks. This will eliminate ALL symmetry regarding
     * empty processors.
     *
     * For example, if we had 3 tasks and 2 processors, the below code goes through these possible scenarios.
     * 1-, 2-, 3-, 12, 23, 13
     * It then calls DFS from these states. In these DFS calls, tasks _cannot_ be placed on an empty processor.
     *
     * Cases such as -1, 21, etc are not re-evaluated. If you consider that 12345 has 5! = 120 permutations,
     * this optimisation "saves" 119 branches from being searched.
     *
     * In detail, the dfsCaller goes through each index=ind from 0 to n, and tries setting it on or off.
     * Bitmask stores whether the previous indices were on or off.
     * By the time that ind = n, 2^n bitmasks would be generated.
     *
     * E.g. if n=3, by the time that ind = 3, bitmask could be 000, 001, 010, 011, 100, 101, 110, 111
     *
     * This recursive method is used to generate all possible combinations of bits being on/off.
     *
     * @param ind The current index that the recursive method is on.
     * @param bitmask The state (on/off) of all the indices before ind.
     */
    private void dfsCaller(int ind, int bitmask) {
        // The method recursively enumerates all possible bitmasks where there are <= p on bits.

        // If there are more than p on bits, return.
        if (Integer.bitCount(bitmask) > numProcessors) return;

        // At the end of the recursion, of n bits, there are some bits that are on.
        // For example: bitmask = b1011 means that tasks 0, 1, and 3 are on.
        //              bitmask = b1100 means that tasks 2, and 3 are on.
        if (ind == numTasks) {
            State state = new State(numTasks, Integer.bitCount(bitmask));
            int processor = 0;
            // For each bit,
            for (int task = 0; task < numTasks; task++) {
                // If the bit is on,
                if ((bitmask & (1 << task)) != 0) {
                    // Add it to the current state by updating:
                    int endTime = input.getTasks().get(task).getTaskTime();
                    state.processorEndTime[processor] = endTime; // processor end time
                    state.taskEndTime[task] = endTime; // task end time
                    state.unassignedTasks--; // number of tasks
                    state.endTime = Math.max(state.endTime, endTime); // overall state end time
                    state.assignedProcessorId[task] = processor; // the processor that this task is assigned to

                    processor++;
                }
            }

            // By here, we have a State that has those 'on' tasks at start time = 0.
            // The state is passed to DFS and DFS handles the rest of the searching.

            dfsExecutor.runDFS(statisticToggle.getDFS(state, bound, revAdjList, taskTimes, informationHolder));
            return;
        }

        // Tries to place the current bit as 'on', only if it has no dependencies
        if (revAdjList.get(ind).size() == 0) {
            dfsCaller(ind+1, bitmask | (1<<ind));
        }
        // Tries to place the current bit as 'off'.
        dfsCaller(ind+1, bitmask);
    }

    @Override
    public InformationHolder getInformationHolder() {
        return informationHolder;
    }
}

