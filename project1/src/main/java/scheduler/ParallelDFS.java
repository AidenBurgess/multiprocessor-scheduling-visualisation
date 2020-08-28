package main.java.scheduler;

import java.util.concurrent.*;

/**
 * ParallelDFS extends DFS and provides a parallelised form of DFS#run
 */
public abstract class ParallelDFS extends DFS {

    // the total number of threads to use
    protected static int _numParallelCores;

    // thread pool that will deal with all the threads
    protected static ThreadPoolExecutor _pool = null;

    public ParallelDFS(State state, Bound bound, DataStructures dataStructures, DFSListener dfsListener) {
        super(state, bound, dataStructures, dfsListener);
    }

    /**
     * Initialises the ThreadPool. Called at the start of Driver.
     * @param numParallelCores
     */
    public static void initialiseThreadPool(int numParallelCores) {
        _numParallelCores = numParallelCores;
        _pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(numParallelCores);
    }
}
