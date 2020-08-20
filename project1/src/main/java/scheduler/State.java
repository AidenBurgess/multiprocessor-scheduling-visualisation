package main.java.scheduler;

/**
 * The State class holds information that defines an allocation of tasks on processors.
 */
public class State {
    protected static final int UNSCHEDULED = -1;
    protected final int numTasks, numProcessors;


    protected int[] assignedProcessorId; // assignedProcessorId[taskId] -> processorId
    protected int[] taskEndTime; // taskEndTime[taskId] -> end time of task
    protected int[] processorEndTime; // processorEndTime[processorId] -> end time of processor

    protected int endTime; // end time of the last processor
    protected int unassignedTasks; // number of tasks still unassigned

    public State(int numTasks, int numProcessors) {
        this.numTasks = numTasks;
        this.numProcessors = numProcessors;

        unassignedTasks = numTasks; // initially n tasks are unassigned
        endTime = 0;

        processorEndTime = new int[numProcessors];
        assignedProcessorId = new int[numTasks];
        taskEndTime = new int[numTasks];

        for (int i = 0; i < numTasks; i++) assignedProcessorId[i] = UNSCHEDULED;
    }

    /**
     * Returns a deep copy of the current State
     *
     * @return a new State instance with the same values.
     */
    public State copy() {
        State next = new State(numTasks, numProcessors);
        for (int i = 0; i < numTasks; i++) {
            next.assignedProcessorId[i] = assignedProcessorId[i];
            next.taskEndTime[i] = taskEndTime[i];
        }
        for (int i = 0; i < numProcessors; i++) {
            next.processorEndTime[i] = processorEndTime[i];
        }

        next.endTime = endTime;
        next.unassignedTasks = unassignedTasks;
        return next;
    }
}
