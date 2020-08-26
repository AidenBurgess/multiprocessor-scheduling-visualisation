package main.java.scheduler;

import java.util.concurrent.*;

public class ParallelDFS extends DFS {
    private static int _numParallelCores;
    private static ThreadPoolExecutor _pool = null;

    public static void initialiseThreadPool(int numParallelCores) {
        _numParallelCores = numParallelCores;
        _pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(numParallelCores);
    }


    public ParallelDFS(State state, Bound bound, DataStructures dataStructures,
                       DFSListener dfsListener) {
        super(state, bound, dataStructures, dfsListener);
    }

    /**
     * tryRun's job is to get passed prevTask, prevProcessor, State and execute a dfs run()
     *
     * @param prevTask
     * @param prevProcessor
     */
    @Override
    protected void run(int prevTask, int prevProcessor) {
        double prob = Math.random();
        boolean split = prob >= 1. / _numParallelCores;
        // cores = 1, prob = 0 (never split)
        // cores = 2, prob = 0.5
        // cores = 3, prob = 0.66
        // the higher the core count, the more likely the split

        if (split && _state._unassignedTasks >= 7) {
            DFS dfs = new DFS(_state.copy(), _bound, _dataStructures, _dfsListener);
            _pool.submit(() -> {
                dfs.run(prevTask, prevProcessor);
            });
        } else {
            super.run(prevTask, prevProcessor);
        }
    }

    @Override
    protected void waitForFinish() {
        _pool.shutdown();

        try {
            _pool.awaitTermination(60000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        _pool = null;
    }
}
