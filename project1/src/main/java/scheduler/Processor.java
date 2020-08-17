package main.java.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Processor {

    private List<TaskNode> _taskNodes;
    private int _processorNum;
    private int _endTime = 0;

    /**
     * @param processorNum : Each processor is identified by a unique processor number
     */
    public Processor(int processorNum) {
        _processorNum = processorNum;
        _taskNodes = new ArrayList<>();
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
            communicationDelayFromParent.put(edge.getSource(), edge.getWeight());
        }

        for(TaskNode parent : parents) {
            if(parent.getProcessor() != this) {
                schedulingDelay = Math.max(schedulingDelay, parent.getEndTime() + communicationDelayFromParent.get(parent) - _endTime);
            }
        }

        taskNode.setStartTime(_endTime + schedulingDelay);

        // Updating the end time of this processor to account for the scheduled task
        _endTime += taskNode.getTaskTime() + schedulingDelay;
        taskNode.setEndTime(_endTime);
        taskNode.scheduleTask();
        taskNode.setProcessor(this);

        // Adding this scheduled task to the list maintained in this processor
        _taskNodes.add(taskNode);
    }

    /**
     * Removes the last scheduled task from this processor
     */
    public void dismountLastTaskNode() {
        TaskNode taskNodeToRemove = _taskNodes.get(_taskNodes.size() - 1);

        // remove the field values of the node that is to be removed
        taskNodeToRemove.clearStartTime();
        taskNodeToRemove.clearEndTime();
        taskNodeToRemove.unscheduleTask();
        taskNodeToRemove.setProcessor(null);

        // Removing the task from the list maintained in this processor
        _taskNodes.remove(taskNodeToRemove);

        // Sets the endtime to the endtime of the last scheduled task
        _endTime = _taskNodes.isEmpty() ? 0 : _taskNodes.get(_taskNodes.size() - 1).getEndTime();
    }

    /**
     * Getter for the current processor number
     * @return int, processor number
     */
    public int getProcessorNum() {
        return _processorNum;
    }

    /**
     * Getter for the end time of the current processor
     * @return int, end time
     */
    public int getEndTime() {
        return _endTime;
    }

    /**
     * Getter for the number of scheduled tasks in the processor
     * @return int, number of scheduled tasks
     */
    public int getScheduledTasksNum() {
        return _taskNodes.size();
    }
}
