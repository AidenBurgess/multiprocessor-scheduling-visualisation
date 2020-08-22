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
     * Responsible for waiting for DFS#run to finish.
     * Control will block in this method until DFS#run has completed and returned.
     * Called at the end of Scheduler#execute.
     */
    void waitForFinish();
}
