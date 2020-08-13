package main.java.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Processor {

    private List<TaskNode> taskNodes;
    private int processorNum;
    private int endTime = 0;

    public Processor(int processorNum) {
        this.processorNum = processorNum;
        taskNodes = new ArrayList<>();
    }

    /**
     * int bestTime = -1;
     * for (Parent parent : node.parents) {
     *      Update the bestTime that you can put it in. e.g. if parent.endTime = 5, and delay = 3, then the best that you can do is 8
     *      If the parent.processor = processor, then delay = 0
     * }
     *
     */

    public void scheduleTask(TaskNode taskNode, List<Edge> incomingEdgesToNode) {
        int schedulingDelay = 0;

        List<TaskNode> parents = taskNode.getDependantOn();
        HashMap<TaskNode, Integer> commDelayFromParent = new HashMap<>();

        for(Edge edge : incomingEdgesToNode) {
            commDelayFromParent.put(edge.getFrom(), edge.getWeight());
        }

        for(TaskNode parent : parents) {
            if(parent.getProcessor() != this) {
                schedulingDelay = Math.max(schedulingDelay, parent.getEndTime() + commDelayFromParent.get(parent) - endTime);
            }
        }

        taskNode.setStartTime(endTime + schedulingDelay);
        endTime += taskNode.getWeight() + schedulingDelay;
        taskNode.setEndTime(endTime);
        taskNode.turnOn();
        taskNode.setProcessor(this);
        taskNodes.add(taskNode);
    }

    // todo ensure that dismounting the last task always works
    public void dismountLastTaskNode() {
        TaskNode taskNodeToRemove = taskNodes.get(taskNodes.size() - 1);
        taskNodeToRemove.setStartTime(0);
        taskNodeToRemove.setEndTime(0);
        taskNodeToRemove.turnOff();
        taskNodeToRemove.setProcessor(null);
        taskNodes.remove(taskNodeToRemove);
    }

    public int getProcessorNum() {
        return processorNum;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getScheduledTasksNum() {
        return taskNodes.size();
    }
}
