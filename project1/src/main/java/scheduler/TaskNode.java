package main.java.scheduler;

import java.util.ArrayList;
import java.util.List;
// todo rename this class and rename the associated methods
public class TaskNode {

    private String name;
    private int startTime;
    private List<TaskNode> dependantOn = new ArrayList<>();
    private int weight;
    private int endTime;
    private Processor processor;
    private boolean isOn;

    public TaskNode(String name, int weight) {
        this.name = name;
        this.weight = weight;
        isOn = false;
    }

    public void addParentTaskNode(TaskNode parentTaskNode) {
        dependantOn.add(parentTaskNode);
    }

    public boolean isDependentOn(TaskNode taskNodeToCheck) {
        return dependantOn.contains(taskNodeToCheck);
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


}
