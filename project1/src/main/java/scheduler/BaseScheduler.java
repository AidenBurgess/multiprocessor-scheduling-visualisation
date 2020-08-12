package main.java.scheduler;

import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseScheduler extends Scheduler {

    private List<Task> taskList;
    private HashMap<String, Node> nodeMap = new HashMap<>();

    public BaseScheduler(TaskGraph taskGraph, int numProcessors) {
        this.numProcessors = numProcessors;
        input = taskGraph;
        taskList = input.tasks; //todo change to method

        for (Task task : taskList) {
            nodeMap.put(task.name, new Node(task.name, task.weight)); //change to method
        }
    }

    public void execute() {
        storeDependencies();

        int numberOfTaskOrders = 0; //todo
        for (int i = 0; i < numberOfTaskOrders; i++) {
            List<Node> nodeList = null; //todo generate order

            if (!validOrder(nodeList)) {
                continue;
            }

            currentState = new Schedule(numProcessors);
            dfs();
        }
    }

    private void storeDependencies() {
        for (Dependency dependency : input.dependencies) {
            String from = dependency.from;
            String to = dependency.to;
            nodeMap.get(to).addParentNode(nodeMap.get(from));
        }
    }

    private boolean validOrder(List<Node> nodeList) {
        for (int i = 0; i < nodeList.size(); i++) {
            for (int j = i + 1; j < nodeList.size(); j++) {
                if (nodeList.get(i).isDependentOn(nodeList.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public HashMap<String, Integer> getStartTimeMap() {
        return null;
    }

    @Override
    public HashMap<String, Integer> getProcessorMap() {
        return null;
    }

    private void dfs() {


        for (int i = 0; i < numProcessors; i++) {

        }

    }

    public void setBestState(Schedule bestSchedule) {
        bestState = bestSchedule;
    }
}

