package main.java.scheduler;

import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.List;

public class BaseScheduler extends Scheduler {

    private FFunction ffunction;
    int bound;

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

    public void execute() {
        storeDependenciesAndEdges(); // todo potentailly something different, depneding on how you choose to do stuff later on - looks fine already (Anubhav)

        currentState = new Schedule(numProcessors);
        dfs();

    }

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

    private void dfs() {
        /**
         * If the current state is full, then that is an answer
         *
         * If not, try put any available, free task into any processor
         */

        // todo think if passing the nodemap as an argument to the isComplete() method is the best thing to do design wise
        //  (because the method name and this argument are not directly related.
        if (currentState.isComplete(taskNodeMap.keySet().size())) {
            // update bound - done
            // update the beststate = currentstate - this can be an issue with deepcopying. we can leave this line for now. - done

            if (currentState.endTime() < bound) {
                bound = currentState.endTime();

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

                //  put node on processor - done
                processor.scheduleTask(taskNode, incomingEdgesMap.get(taskNode));

                //  edit the node, and all the state changes that you need to do - done inside the scheduleTask method
                //  maybe change node start/stop time to match - done inside the scheduleTask method


                //  you are putting task on a processor, you need to know the delay time. - done inside the scheduleTask method

                dfs();
                //  take node off processor - done inside dismountLastNode()
                //  UNDO everything - set to defaults - done inside dismountLastNode()
                processor.dismountLastTaskNode();
            }
        }
    }

    public void setBestState(Schedule bestSchedule) {
        bestState = bestSchedule;
    }
}

