package main.java.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String name;
    private int startTime;
    private List<Node> dependantOn = new ArrayList<>();
    private int weight;
    private int endTime;
}
