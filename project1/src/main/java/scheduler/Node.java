package main.java.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String name;
    private int startTime;
    private List<Node> dependantOn = new ArrayList<>();
    private int weight;
    private int endTime;
    private Processor processor;
    private boolean isOn;

    public Node(String name, int weight) {
        this.name = name;
        this.weight = weight;
        isOn = false;
    }

    public void addParentNode(Node parentNode) {
        dependantOn.add(parentNode);
    }

    public boolean isDependentOn(Node nodeToCheck) {
        return dependantOn.contains(nodeToCheck);
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

    public List<Node> getDependantOn() {
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
