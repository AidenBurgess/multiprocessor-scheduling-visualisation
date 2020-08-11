package main.java.scheduler;

import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseScheduler extends Scheduler {

    public BaseScheduler(TaskGraph taskGraph, int numProcessors) {
        System.out.println("Scheduler created!");
        this.numProcessors = 69;
    }

    public void execute() {
        System.out.println("Execute called!");
    }

    @Override
    public HashMap<String, Integer> getStartTimeMap() {
        return null;
    }

    @Override
    public HashMap<String, Integer> getProcessorMap() {
        return null;
    }

}
