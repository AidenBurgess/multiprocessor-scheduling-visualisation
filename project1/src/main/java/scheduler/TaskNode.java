package main.java.scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Models tasks and records their start time and the processor they are scheduled on.
 */
public class TaskNode {
    private static final int INVALID_TIME = -1;

    private String name;
    private int startTime = INVALID_TIME;
    private List<TaskNode> dependantOn = new ArrayList<>();
    private int weight;
    private int endTime = INVALID_TIME;
    private Processor processor;
    private boolean isOn; // Refers to whether the task is currently scheduled

    public TaskNode(String name, int weight) {
        this.name = name;
        this.weight = weight;
        isOn = false;
    }

    public void addParentTaskNode(TaskNode parentTaskNode) {
        dependantOn.add(parentTaskNode);
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getWeight() {
        return weight;
    }

    public void turnOn() {
        isOn = true;
    }

    public void turnOff() {
        isOn = false;
    }

    public boolean isOn() {
        return isOn;
    }

    public List<TaskNode> getDependantOn() {
        return dependantOn;
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public int getEndTime() {
        return endTime;
    }

    public void clearStartTime() {
        startTime = INVALID_TIME;
    }

    public void clearEndTime() {
        startTime = INVALID_TIME;
    }

    public String getName() {
        return name;
    }
}
