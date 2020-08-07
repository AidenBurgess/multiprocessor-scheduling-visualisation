package main.java.scheduler;

import main.java.dotio.TaskGraph;
import java.util.HashMap;

public class BaseScheduler extends Scheduler {

    public BaseScheduler(TaskGraph taskGraph) {
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
