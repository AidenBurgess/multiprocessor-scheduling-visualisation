package main.java.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private List<Processor> processors;
    private int numProcessors;

    /**
     * @param numProcessors : Number of processors available to schedule tasks on
     */
    public Schedule(int numProcessors) {
        processors = new ArrayList<>();
        this.numProcessors = numProcessors;
        for(int i = 0; i < numProcessors; i++){
            processors.add(new Processor(i+1)); // processor number starts from 1 hence the "i+1"
        }
    }

    /**
     * @return the end time of the schedule by getting the latest processor end time
     */
    public int endTime() {
        int maxProcessorEndTime = Integer.MIN_VALUE;
        for (Processor processor : processors) {
            maxProcessorEndTime = Math.max(maxProcessorEndTime, processor.getEndTime());
        }
        return maxProcessorEndTime;
    }

    /**
     * @param totalTasks
     * @return
     * Checks if all the available tasks have been scheduled
     */
    public boolean isComplete(int totalTasks) {
        int totalScheduledTasks = 0;
        for (Processor processor : processors) {
            totalScheduledTasks += processor.getScheduledTasksNum();
        }
        return totalTasks == totalScheduledTasks;
    }

    public List<Processor> getProcessors() {
        return processors;
    }
}
