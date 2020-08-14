package main.java.scheduler;

import java.util.HashMap;
import java.util.List;

public class SimpleFFunction implements FFunction {

    @Override
    public int evaluate(Schedule s, HashMap<String, TaskNode> nodeMap, HashMap<TaskNode, List<Edge>> incomingEdgesMap) {
        return s.endTime();
    }
}
