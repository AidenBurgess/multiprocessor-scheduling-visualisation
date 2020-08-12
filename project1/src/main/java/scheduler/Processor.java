package main.java.scheduler;

import main.java.dotio.Task;

import java.util.ArrayList;
import java.util.List;

public class Processor {

    private List<Node> nodes;
    private int processorNum;
    private int endTime = 0;

    public Processor(int processorNum) {
        this.processorNum = processorNum;
        nodes = new ArrayList<>();
    }

    /**
     * int bestTime = -1;
     * for (Parent parent : node.parents) {
     *      Update the bestTime that you can put it in. e.g. if parent.endTime = 5, and delay = 3, then the best that you can do is 8
     *      If the parent.processor = processor, then delay = 0
     * }
     *
     */
    public void scheduleTask(Node nodeToBeScheduled, int schedulingDelay) {
        nodeToBeScheduled.setStartTime(endTime + schedulingDelay);
        endTime += nodeToBeScheduled.getWeight() + schedulingDelay;
        nodeToBeScheduled.setEndTime(endTime);
        nodes.add(nodeToBeScheduled);
    }

    public void deleteLastTask() {
        nodes.remove(nodes.size() - 1);
    }

    public int getEndTime() {
        return endTime;
    }


}
