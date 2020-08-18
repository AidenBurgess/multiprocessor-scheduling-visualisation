package main.java.scheduler;

import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseScheduler implements Scheduler {
    private TaskGraph _input;
    private FFunction _ffunction;
    private int _bound;
    private Schedule _currentState;
    private int _numProcessors;
    private HashMap<String, Integer> _startTimeMap = new HashMap<>();
    private HashMap<String, Integer> _processorMap = new HashMap<>();
    private HashMap<String, TaskNode> _taskNodeMap = new HashMap<>();
    private HashMap<TaskNode, List<Edge>> _incomingEdgesMap = new HashMap<>();

    /**
     * @param taskGraph
     * @param numProcessors
     * Parsing the supplied TaskGraph object to produce TaskNode and Edge objects
     */
    public BaseScheduler(TaskGraph taskGraph, int numProcessors) {
        _numProcessors = numProcessors;
        _input = taskGraph;
        _ffunction = new SimpleFFunction();
        _bound = Integer.MAX_VALUE;

        // Insert the tasks into a node map
        for (Task task : _input.getTasks()) {
            _taskNodeMap.put(task.getName(), new TaskNode(task.getName(), task.getTaskTime()));
        }

        // initialise the adjacency list.
        for(String nodeName : _taskNodeMap.keySet()) {
            TaskNode taskNode = _taskNodeMap.get(nodeName);
            _incomingEdgesMap.put(taskNode, new ArrayList<>());
        }
    }

    /**
     * This is the main entry into the scheduling algorithm
     */
    public void execute() {
        storeDependenciesAndEdges();
        _currentState = new Schedule(_numProcessors);
        dfs();
    }

    /**
     * Extracting information from the dependencies and storing them as a list
     * of parent nodes (these are tasks which need to have been scheduled before
     * the child task can be scheduled) inside each child node.
     * If b depends on a then a is the parent and b is the child.
     */
    private void storeDependenciesAndEdges() {
        for (Dependency dependency : _input.getDependencies()) {
            String source = dependency.getSource();
            String dest = dependency.getDest();

            // add the parent (source) of the destination node to the destination node.
            _taskNodeMap.get(dest).addParentTaskNode(_taskNodeMap.get(source));

            TaskNode child = _taskNodeMap.get(dest);

            // add the edge and cost to the incoming edges map of the child.
            List<Edge> incomingEdgesToChild = _incomingEdgesMap.get(child);
            incomingEdgesToChild.add(new Edge(_taskNodeMap.get(source), dependency.getCommunicationTime()));
        }
    }

    /**
     * Recursive dfs method which implements the DFS Branch and Bound algorithm
     */
    private void dfs() {

        // base case: when all the tasks have been scheduled.
        if (_currentState.isComplete(_taskNodeMap.keySet().size())) {
            // If the current schedule is more optimal:
            // (1) Updating the bound
            if (_currentState.endTime() < _bound) {
                _bound = _currentState.endTime();

                // (2) Storing the schedule information
                for (String taskNodeName : _taskNodeMap.keySet()) {
                    _startTimeMap.put(taskNodeName, _taskNodeMap.get(taskNodeName).getStartTime());
                    _processorMap.put(taskNodeName, _taskNodeMap.get(taskNodeName).getProcessor().getProcessorNum());
                }
            }
            return;
        }


        // Ffunction will evaluate the best possible finish time for the current state.
        // If this prediction exceeds bound, prune the branch.
        if (_ffunction.evaluate(_currentState, _taskNodeMap, _incomingEdgesMap) > _bound) return;

        // Below we try and schedule every unscheduled task on every processor
        for (String nodeName : _taskNodeMap.keySet()) {
            TaskNode taskNode = _taskNodeMap.get(nodeName);

            // If the node is on, it cannot be scheduled again.
            if (taskNode.isScheduled()) {
               continue;
            }

            // If the node has some parent dependency that is not scheduled, this node cannot be scheduled yet.
            boolean dependencyMet = true;

            // get the parent nodes that must be scheduled before the current node can be
            for (TaskNode parent : taskNode.getDependantOn()) {
                if(!parent.isScheduled()) {
                    dependencyMet = false;
                    break;
                }
            }

            // if the node cannot be scheduled yet, go to the next node
            if (!dependencyMet) {
                continue;
            }

            // The node can be scheduled. Try scheduling it on to any of the processors.
            for (Processor processor : _currentState.getProcessors()) {
                //  Scheduling the current task on a processor
                processor.scheduleTask(taskNode, _incomingEdgesMap.get(taskNode));

                // Recursive DFS() call
                dfs();

                // The task is dismounted from the processor
                processor.dismountLastTaskNode();
            }
        }
    }

    @Override
    public HashMap<String, Integer> getCurrentStartTimeMap() {
        return null;
    }

    @Override
    public HashMap<String, Integer> getCurrentProcessorMap() {
        return null;
    }

    @Override
    public HashMap<String, Integer> getBestStartTimeMap() {
        return _startTimeMap;
    }

    @Override
    public HashMap<String, Integer> getBestProcessorMap() {
        return _processorMap;
    }

    @Override
    public int getCurrentBound() {
        return 0;
    }

    @Override
    public int getTotalStatesVisited() {
        return 0;
    }

    @Override
    public int getActiveBranches() {
        return 0;
    }
}

