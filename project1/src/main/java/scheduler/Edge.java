package main.java.scheduler;

/**
 * This class models the communication delay information from a parent task.
 * TaskNode objects can have a list of edges which they depend on.
 */
public class Edge {

    private TaskNode _source;
    private int _weight;


    public Edge(TaskNode source, int weight) {
        this._source = source;
        this._weight = weight;
    }

    public TaskNode getSource() {
        return _source;
    }

    public int getWeight() {
        return _weight;
    }
}
