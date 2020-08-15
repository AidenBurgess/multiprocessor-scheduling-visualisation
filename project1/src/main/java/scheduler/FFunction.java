package main.java.scheduler;

import java.util.HashMap;
import java.util.List;

public interface FFunction {
    /**
     * Returns the theoretical best possible endTime to complete the Schedule, given the current Schedule state,
     * taskNodes and dependencies.
     * @param s Current schedule. May be incomplete.
     * @param nodeMap All the taskNodes. All the nodes contain relevant information, such as whether it is currently on and its current endTime.
     * @param incomingEdgesMap An adjacent list that states each taskNode's direct dependencies.
     * @return A prediction of the best endTime if the Schedule is completed. Must be an underestimate.
     */
    int evaluate(Schedule s, HashMap<String, TaskNode> nodeMap, HashMap<TaskNode, List<Edge>> incomingEdgesMap);
}
