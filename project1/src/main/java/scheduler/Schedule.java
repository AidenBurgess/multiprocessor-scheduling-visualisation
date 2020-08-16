package main.java.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private List<Processor> _processors;
    private int _numProcessors;

    /**
     * @param numProcessors : Number of processors available to schedule tasks on
     */
    public Schedule(int numProcessors) {
        _processors = new ArrayList<>();
        _numProcessors = numProcessors;

        // processor number is indexed from 1
        for(int i = 1; i <= numProcessors; i++){

            _processors.add(new Processor(i));
        }
    }

    /**
     * @return the end time of the schedule by getting the latest processor end time
     */
    public int endTime() {
        int maxProcessorEndTime = Integer.MIN_VALUE;
        for (Processor processor : _processors) {
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
        for (Processor processor : _processors) {
            totalScheduledTasks += processor.getScheduledTasksNum();
        }
        return totalTasks == totalScheduledTasks;
    }

    /**
     * Getter for the processors in the schedule
     * @return List<Processor>, list of all processors in the schedule
     */
    public List<Processor> getProcessors() {
        return _processors;
    }
}
