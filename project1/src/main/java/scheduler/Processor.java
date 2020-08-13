package main.java.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Processor {

    private List<TaskNode> taskNodes;
    private int processorNum;
    private int endTime = 0;

    /**
     * @param processorNum : Each processor is identified by a unique processor number
     */
    public Processor(int processorNum) {
        this.processorNum = processorNum;
        taskNodes = new ArrayList<>();
    }

    /**
     * @param taskNode
     * @param incomingEdgesToNode
     * Schedules the given task on this processor
     */
    public void scheduleTask(TaskNode taskNode, List<Edge> incomingEdgesToNode) {
        // schedulingDelay is the difference between the end time of the last task scheduled on this processor and
        // the earliest start time the given task can have due to dependencies
        int schedulingDelay = 0;

        List<TaskNode> parents = taskNode.getDependantOn();
        HashMap<TaskNode, Integer> communicationDelayFromParent = new HashMap<>();

        for(Edge edge : incomingEdgesToNode) {
            communicationDelayFromParent.put(edge.getFrom(), edge.getWeight());
        }

        for(TaskNode parent : parents) {
            if(parent.getProcessor() != this) {
                schedulingDelay = Math.max(schedulingDelay, parent.getEndTime() + communicationDelayFromParent.get(parent) - endTime);
            }
        }

        taskNode.setStartTime(endTime + schedulingDelay);
        endTime += taskNode.getWeight() + schedulingDelay; // Updating the end time of this processor to account for the scheduled task
        taskNode.setEndTime(endTime);
        taskNode.turnOn();
        taskNode.setProcessor(this);
        taskNodes.add(taskNode); // Adding this scheduled task to the list maintained in this processor
    }

    /**
     * Removes the last scheduled task from this processor
     */
    public void dismountLastTaskNode() {
        TaskNode taskNodeToRemove = taskNodes.get(taskNodes.size() - 1);
        taskNodeToRemove.clearStartTime();
        taskNodeToRemove.clearEndTime();
        taskNodeToRemove.turnOff();
        taskNodeToRemove.setProcessor(null);
        taskNodes.remove(taskNodeToRemove); // Removing the task from the list maintained in this processor
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
