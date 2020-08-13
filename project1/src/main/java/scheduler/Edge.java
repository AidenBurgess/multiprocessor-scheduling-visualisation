package main.java.scheduler;

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
