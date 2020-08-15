package main.java.scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Models tasks and records their start time and the processor they are scheduled on.
 */
public class TaskNode {
    private static final int INVALID_TIME = -1;

    private String _name;
    private int _startTime = INVALID_TIME;
    private List<TaskNode> _dependantOn = new ArrayList<>();
    private int _taskTime;
    private int _endTime = INVALID_TIME;
    private Processor _processor;
    private boolean _isScheduled; // Refers to whether the task is currently scheduled

    public TaskNode(String name, int taskTime) {
        _name = name;
        _taskTime = taskTime;
        _isScheduled = false;
    }

    /**
     * Adds a node to be the parent of the current tasknode
     * @param parentTaskNode
     */
    public void addParentTaskNode(TaskNode parentTaskNode) {
        _dependantOn.add(parentTaskNode);
    }

    /**
     * Getter for the start time of the current tasknode
     * @return int, start time
     */
    public int getStartTime() {
        return _startTime;
    }

    /**
     * Setter for the start time of the current task
     * @param startTime
     */
    public void setStartTime(int startTime) {
        this._startTime = startTime;
    }

    /**
     * Setter of the end time of a task
     * @param endTime
     */
    public void setEndTime(int endTime) {
        this._endTime = endTime;
    }

    /**
     * Getter for the task time of the current task
     * @return int, task time
     */
    public int getTaskTime() {
        return _taskTime;
    }

    /**
     * Setter for setting the current task as scheduled on a schedule
     */
    public void scheduleTask() {
        _isScheduled = true;
    }

    /**
     * Getter for setting the current task as scheduled on a schedule
     */
    public void unscheduleTask() {
        _isScheduled = false;
    }

    /**
     * Check if a task has been scheduled
     * @return boolean, scheduled or not
     */
    public boolean isScheduled() {
        return _isScheduled;
    }

    /**
     * Getter for the nodes that the current node depends on
     * @return List<TaskNode> all 'parent' nodes
     */
    public List<TaskNode> getDependantOn() {
        return _dependantOn;
    }

    /**
     * Getter for the processor that this task is scheduled on
     * @return Processor
     */
    public Processor getProcessor() {
        return _processor;
    }

    /**
     * Setter for the processor that the current task is scheduled on
     * @param processor
     */
    public void setProcessor(Processor processor) {
        this._processor = processor;
    }

    /**
     * Getter for the end time of the current node
     * @return int, end time
     */
    public int getEndTime() {
        return _endTime;
    }

    /**
     * Clears the start time and sets it to an invalid time
     */
    public void clearStartTime() {
        _startTime = INVALID_TIME;
    }

    /**
     * Clears the end time and sets it to an invalid time
     */
    public void clearEndTime() {
        _startTime = INVALID_TIME;
    }

    /**
     * Getter for the name of the task
     * @return String, name
     */
    public String getName() {
        return _name;
    }
}
