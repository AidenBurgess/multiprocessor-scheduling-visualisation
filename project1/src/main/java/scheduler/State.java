package main.java.scheduler;

/**
 * The State class holds information that defines an allocation of tasks on processors.
 */
public class State {
    public static final int UNSCHEDULED = -1;
    protected final int n, p;


    protected int[] assignedProcessorId; // assignedProcessorId[taskId] -> processorId
    protected int[] taskEndTime; // taskEndTime[taskId] -> end time of task
    protected int[] processorEndTime; // processorEndTime[processorId] -> end time of processor

    protected int endTime; // end time of the last processor
    protected int unassignedTasks; // number of tasks still unassigned

    public State(int n, int p) {
        this.n = n;
        this.p = p;

        unassignedTasks = n; // initially n tasks are unassigned
        endTime = 0;

        processorEndTime = new int[p];
        assignedProcessorId = new int[n];
        taskEndTime = new int[n];

        for (int i = 0; i < n; i++) assignedProcessorId[i] = UNSCHEDULED;
    }

    /**
     * Returns a deep copy of the current State
     *
     * @return a new State instance with the same values.
     */
    public State copy() {
        State next = new State(n, p);
        for (int i = 0; i < n; i++) {
            next.assignedProcessorId[i] = assignedProcessorId[i];
            next.taskEndTime[i] = taskEndTime[i];
        }
        for (int i = 0; i < p; i++) {
            next.processorEndTime[i] = processorEndTime[i];
        }

        next.endTime = endTime;
        next.unassignedTasks = unassignedTasks;
        return next;
    }
}
