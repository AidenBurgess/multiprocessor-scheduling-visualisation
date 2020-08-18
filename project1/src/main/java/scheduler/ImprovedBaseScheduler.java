package main.java.scheduler;

import javafx.util.Pair;
import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;

public class ImprovedBaseScheduler implements Scheduler {
    int n, p;
    int bound = 1000 * 1000 * 1000;
    TaskGraph input;

    ArrayList<ArrayList<Pair<Integer,Integer>>> adjList, revAdjList;

    HashMap<String, Integer> bestStartTimeMap, bestProcessorMap;


    public ImprovedBaseScheduler(TaskGraph taskGraph, int processors) {
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
        dfs(state);
    }

    private ArrayList<State> getNextStates(State state) {
        ArrayList<State> nextStates = new ArrayList<>();
        for (int task = 0; task < n; task++) {
            if (state.assignedProcessorId[task] == State.UNSCHEDULED) {
                for (int processor = 0; processor < p; processor++) {

                    State nextState = state.copy();
                    boolean ok = true;

                    int nextTaskStartTime = state.processorEndTime[processor];
                    for (Pair<Integer, Integer> dependency : revAdjList.get(task)) {
                        int parent = dependency.getKey();
                        int delay = dependency.getValue();

                        if (state.assignedProcessorId[parent] == State.UNSCHEDULED) {
                            ok = false;
                            break;
                        }
                        if (state.assignedProcessorId[parent] == processor) continue;

                        nextTaskStartTime = Math.max(nextTaskStartTime, state.taskEndTime[parent] + delay);
                    }
                    if (!ok) continue;

                    int nextTaskEndTime = nextTaskStartTime + input.getTasks().get(task).getTaskTime();

                    nextState.taskEndTime[task] = nextTaskEndTime;
                    nextState.assignedProcessorId[task] = processor;
                    nextState.processorEndTime[processor] = nextTaskEndTime;
                    nextState.unassignedTasks--;

                    nextStates.add(nextState);
                }
            }
        }
        return nextStates;
    }

    private void dfs(State state) {
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

        if (state.endTime >= bound + 1) return;

        ArrayList<State> nextStates = getNextStates(state);

        for (State nextState : nextStates) {
            dfs(nextState);
            if (state.endTime >= bound + 1) return;
        }
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

    // inner private class!
    class State {
        public static final int UNSCHEDULED = -1;
        private int n, p;


        protected int[] assignedProcessorId;
        protected int[] taskEndTime;
        protected int[] processorEndTime;
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
}


