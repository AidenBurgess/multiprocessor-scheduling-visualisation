package main.java.scheduler;

import main.java.dotio.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Processor {

    private List<Node> nodes;
    private int processorNum;
    private int endTime = 0;

    public Processor(int processorNum) {
        this.processorNum = processorNum;
        nodes = new ArrayList<>();
    }

    /**
     * int bestTime = -1;
     * for (Parent parent : node.parents) {
     *      Update the bestTime that you can put it in. e.g. if parent.endTime = 5, and delay = 3, then the best that you can do is 8
     *      If the parent.processor = processor, then delay = 0
     * }
     *
     */

    public void scheduleTask(Node node, List<Edge> incomingEdgesToNode) {
        int schedulingDelay = 0;

        List<Node> parents = node.getDependantOn();
        HashMap<Node, Integer> parentEdgeWeightMap = new HashMap<>();

        for(Edge edge : incomingEdgesToNode) {
            parentEdgeWeightMap.put(edge.getFrom(), edge.getWeight());
        }

        for(Node parent : parents) {
            if(parent.isOn() && parent.getProcessor() == this) {
                schedulingDelay = Math.max(schedulingDelay, parent.getEndTime() + parentEdgeWeightMap.get(parent) - endTime);
            }
        }

        node.setStartTime(endTime + schedulingDelay);
        endTime += node.getWeight() + schedulingDelay;
        node.setEndTime(endTime);
        nodes.add(node);;
    }

    public void deleteLastTask() {
        nodes.remove(nodes.size() - 1);
    }

    public int getEndTime() {
        return endTime;
    }


}
