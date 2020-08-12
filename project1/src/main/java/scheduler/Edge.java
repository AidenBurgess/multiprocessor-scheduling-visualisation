package main.java.scheduler;

public class Edge {

    private Node from;
    private int weight;


    public Edge(Node from, int weight) {
        this.from = from;
        this.weight = weight;
    }

    public Node getFrom() {
        return from;
    }

    public int getWeight() {
        return weight;
    }


}
