package main.java.scheduler;

import java.util.*;

public class TopologicalFFunction implements FFunction {
    private HashMap<String, TaskNode> nodeMap;
    private HashMap<TaskNode, List<Edge>> incomingEdgesMap;
    private HashMap<String, Integer> bestEndingTime;

    private int getBestEndingTime(String task) {
        TaskNode taskNode = nodeMap.get(task);
        if (taskNode.isOn()) return taskNode.getEndTime();
        if (!incomingEdgesMap.containsKey(taskNode) || incomingEdgesMap.get(taskNode).size() == 0) return taskNode.getWeight();
        if (bestEndingTime.containsKey(task)) return bestEndingTime.get(task);

        int bestTime = -1;
        for (Edge edge : incomingEdgesMap.get(taskNode)) {
            bestTime = Math.max(bestTime, getBestEndingTime(edge.getFrom().getName()) + taskNode.getWeight());
        }
        bestEndingTime.put(task, bestTime);
        return bestTime;
    }

    @Override
    public int evaluate(Schedule s, HashMap<String, TaskNode> nodeMap, HashMap<TaskNode, List<Edge>> incomingEdgesMap) {
        this.nodeMap = nodeMap;
        this.incomingEdgesMap = incomingEdgesMap;
        bestEndingTime = new HashMap<>();

        int res = -1;
        for (String task : nodeMap.keySet()) {
            res = Math.max(res, getBestEndingTime(task));
        }
        return res;
    }
}
