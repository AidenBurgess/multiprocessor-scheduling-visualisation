package main.java.scheduler;

import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.List;

public class BaseScheduler extends Scheduler {

    private FFunction ffunction;
    int bound;

    /**
     * @param taskGraph
     * @param numProcessors
     * Parsing the supplied TaskGraph object to produce TaskNode and Edge objects
     */
    public BaseScheduler(TaskGraph taskGraph, int numProcessors) {
        this.numProcessors = numProcessors;
        input = taskGraph;
        ffunction = new SimpleFFunction();
        bound = Integer.MAX_VALUE;

        for (Task task : input.tasks) { // todo change to getter
            taskNodeMap.put(task.name, new TaskNode(task.name, task.weight)); //todo change to gettter
        }

        for(String nodeName : taskNodeMap.keySet()) {
            TaskNode taskNode = taskNodeMap.get(nodeName);
            incomingEdgesMap.put(taskNode, new ArrayList<>());
        }
    }

    /**
     * This is the main entry into the scheduling algorithm
     */
    public void execute() {
        storeDependenciesAndEdges();
        currentState = new Schedule(numProcessors);
        dfs();
    }

    /**
     * Extracting information from the dependencies and storing them as a list
     * of parent nodes (these are tasks which need to have been scheduled before
     * the child task can be scheduled) inside each child node.
     * If b depends on a then a is the parent and b is the child.
     */
    private void storeDependenciesAndEdges() {
        for (Dependency dependency : input.dependencies) {
            String from = dependency.from;
            String to = dependency.to;

            taskNodeMap.get(to).addParentTaskNode(taskNodeMap.get(from));

            TaskNode child = taskNodeMap.get(to);
            List<Edge> incomingEdgesToChild = incomingEdgesMap.get(child);
            incomingEdgesToChild.add(new Edge(taskNodeMap.get(from), dependency.weight));
        }
    }

    /**
     * Recursive dfs method which implements the DFS Branch and Bound algorithm
     */
    private void dfs() {
        if (currentState.isComplete(taskNodeMap.keySet().size())) {

            // If the current schedule is more optimal:
            // (1) Updating the bound
            if (currentState.endTime() < bound) {
                bound = currentState.endTime();

                // (2) Storing the schedule information
                for (String taskNodeName : taskNodeMap.keySet()) {
                    startTimeMap.put(taskNodeName, taskNodeMap.get(taskNodeName).getStartTime());
                    processorMap.put(taskNodeName, taskNodeMap.get(taskNodeName).getProcessor().getProcessorNum());
                }
            }
            return;
        }


        // todo F-function
        // the idea is that if we use some heuristic and the schedule is already exceeding the bound, return.
        // this might not be the end!
        // this is a prediction! a strict lower bound.

        if (ffunction.evaluate(currentState) > bound) return;

        // Below we try and schedule every unscheduled task on every processor
        for (String nodeName : taskNodeMap.keySet()) {
            boolean dependencyMet = true;
            TaskNode taskNode = taskNodeMap.get(nodeName);

            if(taskNode.isOn()) {
               continue;
            }

            for(TaskNode parent : taskNode.getDependantOn()) {
                if(!parent.isOn()) {
                    dependencyMet = false;
                    break;
                }
            }

            if(!dependencyMet) {
                continue;
            }

            for (Processor processor : currentState.getProcessors()) {

                //  Scheduling the current task on a processor
                processor.scheduleTask(taskNode, incomingEdgesMap.get(taskNode));

                // Recursive DFS() call
                dfs();

                // The task is dismounted from the current processor
                processor.dismountLastTaskNode();
            }
        }
    }
}

