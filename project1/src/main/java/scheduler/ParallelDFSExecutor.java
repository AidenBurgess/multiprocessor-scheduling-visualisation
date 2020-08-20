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
    ExecutorService pool;
    public ParallelDFSExecutor(int numThreads) {
        pool = Executors.newFixedThreadPool(numThreads);
    }
    @Override
    public void runDFS(DFS dfs) {
        pool.execute(() -> {
            dfs.run();
        });
    }

    @Override
    public void finish()  {
        pool.shutdown();
        try {
            pool.awaitTermination(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Pool closed");
    }
}
