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
