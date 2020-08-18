package main.java.scheduler;

import javafx.util.Pair;
import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ReducedStateScheduler implements Scheduler {
    int n, p;
    int bound = 1000 * 1000 * 1000;
    TaskGraph input;

    ArrayList<ArrayList<Pair<Integer,Integer>>> adjList, revAdjList;

    HashMap<String, Integer> bestStartTimeMap, bestProcessorMap;


    public ReducedStateScheduler(TaskGraph taskGraph, int processors) {
        n = taskGraph.getTasks().size();
        p = processors;
        input = taskGraph;

        adjList = new ArrayList<>(n);
        revAdjList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
            revAdjList.add(new ArrayList<>());
        }

        HashMap<String, Integer> taskNameToIdMap = new HashMap<>();

        for (int i = 0; i < taskGraph.getTasks().size(); i++) {
            taskNameToIdMap.put(taskGraph.getTasks().get(i).getName(), i);
        }

        for (Dependency dependency : taskGraph.getDependencies()) {
            int source = taskNameToIdMap.get(dependency.getSource());
            int dest = taskNameToIdMap.get(dependency.getDest());
            adjList.get(source).add(new Pair<>(dest, dependency.getCommunicationTime()));
            revAdjList.get(dest).add(new Pair<>(source, dependency.getCommunicationTime()));
        }
    }

    @Override
    public void execute() {
        State state = new State(n, p);
        DFS dfs = new DFS(state);
        dfs.go();
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
    public int getTotalStatesVisited() {
        return 0;
    }

    @Override
    public int getCompleteStatesVisited() {
        return 0;
    }

    @Override
    public int getActiveBranches() {
        return 0;
    }

    class DFS {
        State state;


        public DFS(State state) {
            this.state = state;
        }

        private void go() {
            if (state.endTime >= bound) return;

            if (state.unassignedTasks == 0) {
                bestStartTimeMap = new HashMap<>();
                bestProcessorMap = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    Task task = input.getTasks().get(i);
                    bestStartTimeMap.put(task.getName(), state.taskEndTime[i] - task.getTaskTime());
                    bestProcessorMap.put(task.getName(), state.assignedProcessorId[i] + 1); // 1-indexed
                }
                bound = Math.min(bound, state.endTime);
                return;
            }

            if (state.endTime + 1 >= bound) return;

            for (int task = 0; task < n; task++) {
                if (state.assignedProcessorId[task] != State.UNSCHEDULED) continue;
                boolean dependenciesMet = true;
                for (Pair<Integer, Integer> dependency : revAdjList.get(task)) {
                    int parent = dependency.getKey();
                    if (state.assignedProcessorId[parent] == State.UNSCHEDULED) {
                        dependenciesMet = false;
                        break;
                    }
                }
                if (!dependenciesMet) continue;

                for (int processor = 0; processor < p; processor++) {
                    int nextTaskStartTime = state.processorEndTime[processor];
                    for (Pair<Integer, Integer> dependency : revAdjList.get(task)) {
                        int parent = dependency.getKey();
                        int delay = dependency.getValue();

                        if (state.assignedProcessorId[parent] == processor) continue;

                        nextTaskStartTime = Math.max(nextTaskStartTime, state.taskEndTime[parent] + delay);
                    }

                    int nextTaskEndTime = nextTaskStartTime + input.getTasks().get(task).getTaskTime();
                    int processorPrevEndTime = state.processorEndTime[processor];
                    int prevEndTime = state.endTime;

                    state.taskEndTime[task] = nextTaskEndTime;
                    state.assignedProcessorId[task] = processor;
                    state.processorEndTime[processor] = nextTaskEndTime;
                    state.unassignedTasks--;
                    state.endTime = Math.max(state.endTime, nextTaskEndTime);

                    go();

                    state.taskEndTime[task] = State.UNSCHEDULED;
                    state.assignedProcessorId[task] = State.UNSCHEDULED;
                    state.processorEndTime[processor] = processorPrevEndTime;
                    state.unassignedTasks++;
                    state.endTime = prevEndTime;

                    if (state.endTime + 1 >= bound) return;
                }

            }
        }
    }
}



// inner private class!
class State {
    public static final int UNSCHEDULED = -1;
    private int n, p;


    protected int[] assignedProcessorId;
    protected int[] taskEndTime;
    protected int[] processorEndTime;
//        protected int[] taskInDegree;

    protected int endTime, unassignedTasks;

    public State(int n, int p) {
        this.n = n;
        this.p = p;

        unassignedTasks = n;
        endTime = 0;

        processorEndTime = new int[p];
        assignedProcessorId = new int[n];
        taskEndTime = new int[n];

        for (int i = 0; i < n; i++) assignedProcessorId[i] = UNSCHEDULED;
    }

    /**
     * Deep copy!
     * @return
     */
    public State copy() {
        State next = new State(n, p);
        for (int i = 0; i < n; i++) {
            next.assignedProcessorId[i] = assignedProcessorId[i];
            next.taskEndTime[i] = taskEndTime[i];
        }
        for (int i = 0; i < p; i++) {
            next.processorEndTime[i] = processorEndTime[i];
        }

        next.endTime = endTime;
        next.unassignedTasks = unassignedTasks;
        return next;
    }


}
