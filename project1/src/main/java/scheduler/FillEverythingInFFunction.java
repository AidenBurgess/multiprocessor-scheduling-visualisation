package main.java.scheduler;

import java.util.HashMap;
import java.util.List;

/**
 * The strategy for this FFunction is to see how much total time there is left, and pack the times in as tight
 * as possible onto the current Schedule. E.g.
 *
 * Schedule:
 * P1: XXX
 * P2: XXXXXXX
 * P3: X
 *
 * Unscheduled task weights: 4, 5, 3
 * The total time is worked out, 12, and 'stuffed' into the schedule.
 *
 * Estimated Schedule:
 * P1: XXX-----
 * P2: XXXXXXX-
 * P3: X------
 *
 * Notice that after 'stuffing' 12 units of time in, the best endTime that the Schedule can have is 8.
 * Also notice that when 'stuffing' the original task times are not necessarily together (this is a computationally
 * easier problem).
 *
 * This evaluate method, for this case, will return 8.
 */
public class FillEverythingInFFunction implements FFunction {
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
        // First, calculate the total time left of the unscheduled tasks.
        int totalTimeLeft = 0;
        for (TaskNode taskNode : nodeMap.values()) {
            if (!taskNode.isScheduled()) totalTimeLeft += taskNode.getTaskTime();
        }
        // 'Stuffing'. For each processor, stuff as much time as you can without exceeding the biggest endTime.
        for (Processor processor : s.getProcessors()) {
            totalTimeLeft -= (s.endTime() - processor.getEndTime());
        }

        // This is the case that we do not have enough time to 'stuff' to exceed the endTime.
        if (totalTimeLeft <= 0) return s.endTime();

        // We have exceeded s.endTime. The times will be spread evenly across the processors.
        // We will round up.
        // 13 / 4 results in at least 4 extra time.
        // 20 / 4 results in 5 extra time
        int processors = s.getProcessors().size();
        return totalTimeLeft % processors == 0 ? s.endTime() + totalTimeLeft / processors : s.endTime() + totalTimeLeft / processors + 1;
    }
}
