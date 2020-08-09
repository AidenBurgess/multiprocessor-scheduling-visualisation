package main.java.dotio;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TaskGraph {
    private ArrayList<Task> _tasks; // name and a taskTime
    private ArrayList<Dependency> _dependencies; // node a, node b, communicationTime

    // method to input values to the tasks

    // @todo constructor
    public TaskGraph() {

    }

    public void insertTask(Task task) {
        _tasks.add(task);
    }

    public void insertDependency(Dependency dependency) {
        _dependencies.add(dependency);
    }

    public ArrayList<Task> getTasks() {
        return _tasks;
    }

    public ArrayList<Dependency> getDependencies() {
        return _dependencies;
    }
}
