package main.java.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String name;
    private int startTime;
    private List<Node> dependantOn = new ArrayList<>();
    private int weight;
    private int endTime;

    public Node(String name, int weight) {
        this.name = name;
        this.weight = weight;
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

}
