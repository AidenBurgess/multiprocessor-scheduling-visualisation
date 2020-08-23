package main.java.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Provides a parallelized version of dfs.run().
 * Responsible for creating a 'thread pool' and executes dfs.run() in the pool.
 * Responsible for waiting for all the Runnables to be executed and handles thread exceptions.
 */
public class ParallelDFSExecutor implements DFSExecutor {
    ExecutorService _pool;
    public ParallelDFSExecutor(int numParallelCores) {
        _pool = Executors.newFixedThreadPool(numParallelCores);
    }
    @Override
    public void runDFS(DFS dfs) {
        _pool.execute(dfs::run);
    }

    @Override
    public void waitForFinish()  {
        _pool.shutdown();
        try {
            _pool.awaitTermination(1000, TimeUnit.SECONDS); // todo timeout?
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
