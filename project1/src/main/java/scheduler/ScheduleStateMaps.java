package main.java.scheduler;

import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.HashMap;

public class ScheduleStateMaps {
    private HashMap<String, Integer> _currentStartTimeMap, _currentProcessorMap, _bestStartTimeMap, _bestProcessorMap;
    private TaskGraph _taskGraph;
    private int _bestBound = 0;

    public ScheduleStateMaps(State bestState, State currentState, TaskGraph taskGraph) {
        _taskGraph = taskGraph;
        _currentStartTimeMap = getStartTimeMap(currentState);
        _currentProcessorMap = getProcessorMap(currentState);
        _bestStartTimeMap = getStartTimeMap(bestState);
        _bestProcessorMap = getProcessorMap(bestState);

        _bestBound = bestState._endTime;
    }

    HashMap<String, Integer> getStartTimeMap(State state) {
        HashMap<String, Integer> map = new HashMap<>();
        if (state == null) return map;

        for (int i = 0; i < state._numTasks; i++) {
            // Do not put a task that is unassigned on this map.
            if (state._assignedProcessorId[i] == State.UNSCHEDULED) continue;

            Task task = _taskGraph.getTasks().get(i);
            map.put(task.getName(), state._taskEndTime[i] - task.getTaskTime());
        }
        return map;
    }

    HashMap<String, Integer> getProcessorMap(State state) {
        HashMap<String, Integer> map = new HashMap<>();
        if (state == null) return map;

        for (int i = 0; i < state._numTasks; i++) {
            // Do not put a task that is unassigned on this map.
            if (state._assignedProcessorId[i] == State.UNSCHEDULED) continue;

            Task task = _taskGraph.getTasks().get(i);
            map.put(task.getName(), state._assignedProcessorId[i] + 1);
        }
        return map;
    }

    public HashMap<String, Integer> getCurrentStartTimeMap() {
        return _currentStartTimeMap;
    }

    public HashMap<String, Integer> getCurrentProcessorMap() {
        return _currentProcessorMap;
    }

    public HashMap<String, Integer> getBestStartTimeMap() {
        return _bestStartTimeMap;
    }

    public HashMap<String, Integer> getBestProcessorMap() {
        return _bestProcessorMap;
    }

    public int getBestBound() {
        return _bestBound;
    }
}
