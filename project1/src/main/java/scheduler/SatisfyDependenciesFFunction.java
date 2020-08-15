package main.java.scheduler;

import java.util.*;

/**
 * The strategy for this FFunction is to imagine "What if we had unlimited processors and no delay time?".
 * The only restriction would be the dependencies.
 *
 * The endTime for each TaskNode will depend on the max endTime of all its parents. E.g
 *
 * If the schedule has T1 and T2 set:
 *
 * S1: 111
 * S2: 2
 *
 * And we know that 1->3 and 2->3 and 3 has weight 4, we would get
 *
 * S1: 1113333
 * S2: 2
 *
 * If we also know that 3->4, and 4 has weight 1, we would get
 *
 * S1: 1113334
 * S2: 2
 *
 * If we also know that 2->5 and 2->6, and 5 and 6 both have weight 2, we would get
 *
 * S1: 1113334
 * S2: 255
 * S3:  66
 *
 * Notice that the processors/delay time constraints is not considered. We are trying to schedule each task as early as
 * we can while satisfying all dependencies.
 */
public class SatisfyDependenciesFFunction implements FFunction {
    private HashMap<String, TaskNode> nodeMap;
    private HashMap<TaskNode, List<Edge>> incomingEdgesMap;
    private HashMap<String, Integer> bestEndingTime;

    /**
     * Returns the best possible ending time by considering its parent's end times.
     * @param task Task to calculate
     * @return the best endTime that it could finish by, considering all its dependencies.
     */
    private int getBestEndingTime(String task) {
        TaskNode taskNode = nodeMap.get(task);

        // If the taskNode is already scheduled, return that endTime.
        if (taskNode.isScheduled()) return taskNode.getEndTime();
        // If the taskNode has no parents, it can be placed at startTime = 0.
        if (!incomingEdgesMap.containsKey(taskNode) || incomingEdgesMap.get(taskNode).size() == 0) return taskNode.getTaskTime();
        // If the method has calculated this task before, return the previously calculated result (memoisation).
        if (bestEndingTime.containsKey(task)) return bestEndingTime.get(task);

        // Place the task as early as possible, but consider the parent.
        int bestTime = taskNode.getTaskTime();
        for (Edge edge : incomingEdgesMap.get(taskNode)) {
            bestTime = Math.max(bestTime, getBestEndingTime(edge.getFrom().getName()) + taskNode.getTaskTime());
        }

        // Memoise
        bestEndingTime.put(task, bestTime);
        return bestTime;
    }

    /**
     * Returns the theoretical best possible endTime to complete the Schedule, given the current Schedule state,
     * taskNodes and dependencies.
     * @param s Current schedule. May be incomplete.
     * @param nodeMap All the taskNodes. All the nodes contain relevant information, such as whether it is currently on and its current endTime.
     * @param incomingEdgesMap An adjacent list that states each taskNode's direct dependencies.
     * @return A prediction of the best endTime if the Schedule is completed. Must be an underestimate.
     */
    @Override
    public int evaluate(Schedule s, HashMap<String, TaskNode> nodeMap, HashMap<TaskNode, List<Edge>> incomingEdgesMap) {
        this.nodeMap = nodeMap;
        this.incomingEdgesMap = incomingEdgesMap;
        bestEndingTime = new HashMap<>();

        // go through each TaskNode - the result is the biggest endTime that it COULD be scheduled on.
        int res = -1;
        for (String task : nodeMap.keySet()) {
            res = Math.max(res, getBestEndingTime(task));
        }
        return res;
    }
}
