package main.java.scheduler;

import java.util.concurrent.TimeUnit;

public class ProbabilisticParallelDFS extends ParallelDFS {
    public ProbabilisticParallelDFS(State state, Bound bound, DataStructures dataStructures, DFSListener dfsListener) {
        super(state, bound, dataStructures, dfsListener);
    }

    protected void run(int prevTask, int prevProcessor) {
        double prob = Math.random();
        boolean split = prob >= 1. / _numParallelCores;
        // cores = 1, prob = 0 (never split)
        // cores = 2, prob = 0.5
        // cores = 3, prob = 0.66
        // the higher the core count, the more likely the split

        if (_state._unassignedTasks >= 6 && split) {
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
