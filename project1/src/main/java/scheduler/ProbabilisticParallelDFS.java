package main.java.scheduler;

import java.util.concurrent.TimeUnit;

/**
 * Uses probability to determine whether the current DFS should be split.
 * On split, a DFS (original, not ParallelDFS) instance is made to search through the branch
 */
public class ProbabilisticParallelDFS extends ParallelDFS {
    public ProbabilisticParallelDFS(State state, Bound bound, DataStructures dataStructures, DFSListener dfsListener) {
        super(state, bound, dataStructures, dfsListener);
    }

    /**
     * Runs dfs and with a specific probability, will create a thread.
     * Otherwise, run dfs as normal.
     * @param prevTask
     * @param prevProcessor
     */
    protected void run(int prevTask, int prevProcessor) {
        double prob = Math.random();
        // We should split if prob is less than 1/cores.
        // E.g. if cores = 2, chance = 50%
        //      if cores = 3, chance = 66%
        // The more cores, the most favourable a split is.
        boolean split = prob <= (1 - 1. / _numParallelCores);

        // Can only split if there are at least half the tree left to search
        if (_state._unassignedTasks >= (_state._numTasks / 2) && split) {
            DFS dfs = new DFS(_state.copy(), _bound, _dataStructures, _dfsListener);
            _pool.submit(() -> {
                dfs.run(prevTask, prevProcessor);
            });
        } else {
            super.run(prevTask, prevProcessor);
        }
    }

    /**
     * on dfs finish, shuts down the thread pool.
     */
    @Override
    protected void waitForFinish() {
        // When control reaches here, all the threads will be created.
        // Pool shutdown closes the pool from accepting new tasks
        _pool.shutdown();
        // Control blocks here until all tasks are completed.
        try {
            _pool.awaitTermination(60000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        _pool = null;
    }


}
