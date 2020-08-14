package main.java.scheduler;

import java.util.HashMap;
import java.util.List;

public class FillEverythingInFFunction implements FFunction {
    @Override
    public int evaluate(Schedule s, HashMap<String, TaskNode> nodeMap, HashMap<TaskNode, List<Edge>> incomingEdgesMap) {
        int totalTimeLeft = 0;
        for (TaskNode taskNode : nodeMap.values()) {
            if (!taskNode.isOn()) totalTimeLeft += taskNode.getWeight();
        }
        int biggestProcessorTime = -1;
        for (Processor processor : s.getProcessors()) {
            biggestProcessorTime = Math.max(biggestProcessorTime, processor.getEndTime());
        }

        // I have totalTimeLeft of time. I will fill these in to the "holes"
        for (Processor processor : s.getProcessors()) {
            totalTimeLeft -= (biggestProcessorTime - processor.getEndTime());
        }

        // If there is any time left, I will distribute it among all the processors fairly.
        if (totalTimeLeft <= 0) return s.endTime();

        // Rounding up : 13 / 4 results in at least 4 time, 20 / 4 results in 5 time
        int processors = s.getProcessors().size();
        return totalTimeLeft % processors == 0 ? totalTimeLeft / processors : totalTimeLeft / processors + 1;
    }
}
