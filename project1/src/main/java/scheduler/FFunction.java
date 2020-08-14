package main.java.scheduler;

import java.util.HashMap;
import java.util.List;

public interface FFunction {
    int evaluate(Schedule s, HashMap<String, TaskNode> nodeMap, HashMap<TaskNode, List<Edge>> incomingEdgesMap);
}
