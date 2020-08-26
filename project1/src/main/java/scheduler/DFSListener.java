package main.java.scheduler;

/**
 * Listens to events from DFS.
 */
public interface DFSListener {
    /**
     * Called on entry of DFS
     */
    void onDFSEntry();

    /**
     * Called on exit of DFS
     */
    void onDFSExit();

    /**
     * Called when a completed schedule is found
     */
    void onCompleteSchedule(State state, Bound bound);

    /**
     * Called when a partial schedule is found
     */
    void onPartialSchedule(State state, Bound bound);
}
