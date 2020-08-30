package main.java.scheduler;

import main.java.dotio.TaskGraph;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides a way for other classes to access the Scheduler's information.
 * Acts as a middle-man. Scheduler class will update this holder's information, and external classes
 * can retrieve information from this holder.
 *
 * Responsible for managing thread-safety if relevant.
 *
 */
public class InformationHolder {
    public static int READY = 0, RUNNING = 1, FINISHED = 2, ABORTED = 3;

    // stats for the visualisation class.
    private AtomicLong _activeBranches;
    private long _totalStates;
    private long _completeStates;
    private long _currentBound;
    private int _schedulerStatus;

    // the task graph and the current and best states.
    private TaskGraph _taskGraph;
    private State _currentState, _bestState;

    public InformationHolder(TaskGraph taskGraph) {
        _taskGraph = taskGraph;
        _activeBranches = new AtomicLong(0);
        _totalStates = 0;
        _completeStates = 0;
        _currentBound = 0;
        _schedulerStatus = READY;
    }

    // --------------- Update the active branches, total states, and completed states ------------- //
    public void incrementActiveBranches() {
        _activeBranches.incrementAndGet();
    }

    public void incrementTotalStates() {
        _totalStates++;
    }

    public void decrementActiveBranches() {
        _activeBranches.decrementAndGet();
    }

    public void incrementCompleteStates() {
        _completeStates++;
    }

    // ------------------ Getters and Setters ------------------- //

    public int getSchedulerStatus() {
        return _schedulerStatus;
    }

    public void setSchedulerStatus(int status) {
        _schedulerStatus = status;
    }

    public long getActiveBranches() {
        return _activeBranches.get();
    }

    public long getTotalStates() {
        return _totalStates;
    }

    public long getCompleteStates() {
        return _completeStates;
    }

    public void setCurrentBound(int bound) {
        _currentBound = bound;
    }

    public long getCurrentBound() {
        return _currentBound;
    }

    public void setBestState(State state) {
        _bestState = state;
    }

    public void setCurrentState(State state) {
        _currentState = state;
    }

    public ScheduleStateMaps getScheduleStateMaps() {
        return new ScheduleStateMaps(_bestState, _currentState, _taskGraph);
    }
}
