package main.java.scheduler;

/**
 * This class models the communication delay information from a parent task.
 * TaskNode objects can have a list of edges which they depend on.
 */
public class Edge {

    private TaskNode from;
    private int weight;


    public Edge(TaskNode from, int weight) {
        this.from = from;
        this.weight = weight;
    }

    public TaskNode getFrom() {
        return from;
    }

    public int getWeight() {
        return weight;
    }
}
