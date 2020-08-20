package main.java.scheduler;

/**
 * A normal, one-thread implementation of DFSExecutor.
 * Simply calls dfs.run().
 */
public class NormalDFSExecutor implements DFSExecutor {
    @Override
    public void runDFS(DFS dfs) {
        dfs.run();
    }

    @Override
    public void finish() {

    }
}
