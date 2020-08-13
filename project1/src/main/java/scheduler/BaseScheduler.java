package main.java.scheduler;

import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseScheduler extends Scheduler {

    private HashMap<String, Node> nodeMap = new HashMap<>();
    private HashMap<Node, List<Edge>> edgeMap = new HashMap<>();

    private FFunction ffunction;
    int bound;

    public BaseScheduler(TaskGraph taskGraph, int numProcessors) {
        this.numProcessors = numProcessors;
        input = taskGraph;
        ffunction = new SimpleFFunction();
        bound = Integer.MAX_VALUE;

        for (Task task : input.tasks) { // todo change to getter
            nodeMap.put(task.name, new Node(task.name, task.weight)); //todo change to gettter
        }

        for(String nodeName : nodeMap.keySet()) {
            Node node = nodeMap.get(nodeName);
            edgeMap.put(node, new ArrayList<>());
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

            nodeMap.get(to).addParentNode(nodeMap.get(from));

            Node child = nodeMap.get(to);
            List<Edge> incomingEdgesToChild = edgeMap.get(child);
            incomingEdgesToChild.add(new Edge(nodeMap.get(from), dependency.weight));
        }
    }

    //todo check if needs to be deleted
    @Override
    public HashMap<String, Integer> getStartTimeMap() {
        return null;
    }

    @Override
    public HashMap<String, Integer> getProcessorMap() {
        return null;
    }

    private void dfs() {
        /**
         * If the current state is full, then that is an answer
         *
         * If not, try put any available, free task into any processor
         */

        if (currentState.isComplete()) {
            // todo update bound
            // todo update the beststate = currentstate - this can be an issue with deepcopying. we can leave this line for now.
            // Option 1 - If we create another class "NodeStateDetails", then how about a hashmap like --> "HashMap <String, NodeStateDetails> bestState"
            // is made to store the best state?
            // Option 2 - Otherwise if the Dotio class needs a TaskGraph object for the write() method, then how about we just store a list of
            // nodes everytime a bestState is found and then just before the termination of the algorithm we cycle through all the
            // nodes, convert them into tasks, and then create a TaskGraph object. One issue is that right now a Task object stores no info.
            // about its start time or processor. So the UML diagram will need to be changed.
            return;
        }


        // todo F-function
        // the idea is that if we use some heuristic and the schedule is already exceeding the bound, return.
        // this might not be the end!
        // this is a prediction! a strict lower bound.

        if (ffunction.evaluate(currentState) > bound) return;


        for (String nodeName : nodeMap.keySet()) {

            boolean dependencyMet = true;

            Node node = nodeMap.get(nodeName);

            if(node.isOn()) {
               continue;
            }

            for(Node parent : node.getDependantOn()) {
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
                processor.scheduleTask(node, edgeMap.get(node));

                //  edit the node, and all the state changes that you need to do - done inside the scheduleTask method
                //  maybe change node start/stop time to match - done inside the scheduleTask method


                //  you are putting task on a processor, you need to know the delay time. - done inside the scheduleTask method

                dfs();
                //  take node off processor - done inside dismountLastNode()
                //  UNDO everything - set to defaults - done inside dismountLastNode()
                processor.dismountLastNode();
            }
        }
    }

    public void setBestState(Schedule bestSchedule) {
        bestState = bestSchedule;
    }
}

