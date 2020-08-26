package main.java.scheduler;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ParallelDFS extends DFS {
    protected static int _numParallelCores;
    protected static ThreadPoolExecutor _pool = null;

    public ParallelDFS(State state, Bound bound, DataStructures dataStructures, DFSListener dfsListener) {
        super(state, bound, dataStructures, dfsListener);
    }

    public static void initialiseThreadPool(int numParallelCores) {
        _numParallelCores = numParallelCores;
        _pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(numParallelCores);
    }
}
