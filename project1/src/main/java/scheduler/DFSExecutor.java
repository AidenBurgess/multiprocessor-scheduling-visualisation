package main.java.scheduler;

/**
 * This interface is responsible for executing DFS.run(), when given the DFS object
 * This allows flexibility when choosing an executor that uses multithreading/no multithreading
 */
public interface DFSExecutor {
    /**
     * Responsible for executing dfs.run()
     * @param dfs The DFS object to call DFS#run on
     */
    void runDFS(DFS dfs);

    /**
     * Responsible for any cleanups if necessary.
     * Called at the end of Scheduler#execute.
     */
    void finish();
}
