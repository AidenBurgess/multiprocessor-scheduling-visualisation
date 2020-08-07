package main.java.scheduler;

import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.concurrent.ThreadPoolExecutor;

public class Scheduler {
    TaskGraph input;
    int numProcessors;
    Schedule currentState;
    Schedule bestState;
    ThreadPoolExecutor threadPool;

    public Scheduler(TaskGraph taskGraph) {
        System.out.println("Scheduler created!");
    }

    public void execute() {
        System.out.println("Execute called!");
    }

}
