package main.java.dotio;

import java.util.ArrayList;

/**
 * This class is an object that contains a list of tasks and dependencies.
 */
public class TaskGraph {
    private String _name;
    private ArrayList<Task> _tasks; // name and a taskTime
    private ArrayList<Dependency> _dependencies; // node a, node b, communicationTime

    /**
     * TaskGraph constructor that includes a specified name upon creation.
     * @param name The name to be given to the graph.
     */
    public TaskGraph(String name) {
        _name = name;
        _tasks = new ArrayList<>();
        _dependencies = new ArrayList<>();
    }

    /**
     * TaskGraph constructor with no specified name upon creation.
     */
    public TaskGraph() {
        _tasks = new ArrayList<>();
        _dependencies = new ArrayList<>();
    }

    /**
     * Add a task (node) to the graph.
     * @param task The Task object being added.
     */
    public void insertTask(Task task) {
        _tasks.add(task);
    }

    /**
     * Add a dependency (edge) to the graph.
     * @param dependency The Dependency object to be added to the graph.
     */
    public void insertDependency(Dependency dependency) {
        _dependencies.add(dependency);
    }

    // ------------------ Getters and Setters ------------------- //

    public void setName(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public ArrayList<Task> getTasks() {
        return _tasks;
    }

    public ArrayList<Dependency> getDependencies() {
        return _dependencies;
    }
}
