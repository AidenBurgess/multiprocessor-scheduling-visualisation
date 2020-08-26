package main.java.scheduler;

import java.util.concurrent.atomic.AtomicInteger;

public class RecursiveParallelDFS extends ParallelDFS {
    public RecursiveParallelDFS(State state, Bound bound, DataStructures dataStructures, DFSListener dfsListener) {
        super(state, bound, dataStructures, dfsListener);
    }

    private static AtomicInteger _taskCount = new AtomicInteger(0);

    @Override
    protected void run(int prevTask, int prevProcessor) {
        if (_state._unassignedTasks >= (_state._numTasks/2) && _taskCount.get() < _numParallelCores) {
            _taskCount.incrementAndGet();
            DFS dfs = new RecursiveParallelDFS(_state.copy(), _bound, _dataStructures, _dfsListener);
            _pool.submit(() -> {
                dfs.run(prevTask, prevProcessor);
                _taskCount.decrementAndGet();
            });
        } else {
            super.run(prevTask, prevProcessor);
        }
    }

    @Override
    protected void waitForFinish() {
        while (_taskCount.get() != 0) {
            // wait
        }
        _pool.shutdown();
        _pool = null;
    }

}
