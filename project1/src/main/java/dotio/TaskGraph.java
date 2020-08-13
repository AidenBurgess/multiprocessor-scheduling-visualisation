package main.java.dotio;

import java.util.ArrayList;

public class TaskGraph {
    private String _name;
    private ArrayList<Task> _tasks; // name and a taskTime
    private ArrayList<Dependency> _dependencies; // node a, node b, communicationTime

    // method to input values to the tasks
    public TaskGraph(String name) {
        _name = name;
        _tasks = new ArrayList<>();
        _dependencies = new ArrayList<>();
    }

    public void insertTask(Task task) {
        _tasks.add(task);
    }

    public void insertDependency(Dependency dependency) {
        _dependencies.add(dependency);
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
