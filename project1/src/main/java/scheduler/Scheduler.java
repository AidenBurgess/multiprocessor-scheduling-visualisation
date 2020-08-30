package main.java.scheduler;

/**
 * Defines responsibilities of a Scheduler instance.
 */
public interface Scheduler {
    /**
     * Upon receiving the TaskGraph as an input, execute() starts running the search algorithm.
     * Control blocks until the result is found.
     */
    public void execute();

    /**
     * Provides a way for external classes to receive this Scheduler's information.
     * @return an InformationHolder instance that provides methods to retrieve information
     */
    public InformationHolder getInformationHolder();
}
