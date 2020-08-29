package main.java.validitychecker;

import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.exception.ValidityCheckerException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * this class checks whether and output schedule is valid. It will check
 * the dependencies of the children tasks and ensure they are scheduled
 * when allowed and then check if there are any overlap of tasks on the
 * same processor.
 */
public class ValidityChecker {

    private ArrayList<Task> _tasks;
    private ArrayList<Dependency> _dependencies;
    private HashMap<String, Integer> _bestStartTimeMap;
    private HashMap<String, Integer> _bestProcessorMap;
    private HashMap<String, Integer> _taskStartTime;

    public ValidityChecker(ArrayList<Task> tasks, ArrayList<Dependency> dependencies, HashMap<String, Integer> bestProcessorMap, HashMap<String, Integer> bestStartTimeMap) {
        _tasks = tasks;
        _dependencies = dependencies;
        _bestStartTimeMap = bestStartTimeMap;
        _bestProcessorMap = bestProcessorMap;

        _taskStartTime = new HashMap<String, Integer>();

        // intialise the task start times
        for (Task task : tasks) {
            _taskStartTime.put(task.getName(), task.getTaskTime());
        }
    }

    /**
     * checks if a final schedule is valid.
     */
    public void check() {

        // check each node exists in this schedule
        for (Task task : _tasks) {
            if (!_bestProcessorMap.containsKey(task.getName()) || !_bestStartTimeMap.containsKey(task.getName())) {
                throw new ValidityCheckerException(task.getName() + " does not exist in the final schedule.");
            }
        }

        // check each dependency
        for (Dependency dependency : _dependencies) {

            String source = dependency.getSource();
            String dest = dependency.getDest();
            int communicationTime = dependency.getCommunicationTime();
            int delay = 0;

            // if they are on different processors, add a delay
            if (!_bestProcessorMap.get(source).equals(_bestProcessorMap.get(dest))) {
                delay = communicationTime;
            }

            // get the parent end time and the possible child start time.
            int parentEndTime = _bestStartTimeMap.get(source) + _taskStartTime.get(source) + delay;
            int childStartTime = _bestStartTimeMap.get(dest);

            // check if the child starts before the parent finishes
            if (childStartTime < parentEndTime) {
                throw new ValidityCheckerException("A dependency was not fulfilled.");
            }
        }

        // check for overlap between tasks
        for (Task taskA : _tasks) {
            for (Task taskB : _tasks) {
                // if the tasks are the same, continue
                if (taskA.equals(taskB)) {
                    continue;
                }

                String nameA = taskA.getName();
                String nameB = taskB.getName();

                if (!_bestProcessorMap.get(nameA).equals(_bestProcessorMap.get(nameB))) {
                    continue;
                }

                // check if start time overlaps end time and vice versa
                int taskAStartTime = _bestStartTimeMap.get(nameA);
                int taskBStartTime = _bestStartTimeMap.get(nameB);
                int taskAEndTime = taskAStartTime + taskA.getTaskTime();
                int taskBEndTime = taskBStartTime + taskB.getTaskTime();

                // if there is any overlap return false
                if (taskAStartTime < taskBEndTime && taskBStartTime < taskAEndTime) {
                    throw new ValidityCheckerException("There are overlapping tasks.");
                }
            }
        }
    }
}
