package main.java.dotio;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TaskGraph {
    private String _taskGraphName;
    private ArrayList<Task> _tasks; // name and a taskTime
    private ArrayList<Dependency> _dependencies; // node a, node b, communicationTime

    // method to input values to the tasks

    // @todo constructor
    public TaskGraph(String taskGraphName, ArrayList<Task> tasks, ArrayList<Dependency> dependencies) {
        _tasks = tasks;
        _dependencies = dependencies;
        _taskGraphName = taskGraphName;
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

    public String getName() {
        return _taskGraphName;
    }
}
