package main.java.scheduler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recursively tries to split task into new thread if:
 * - Active threads < totalCores
 * - In the first half of the tree
 */
public class RecursiveParallelDFS extends ParallelDFS {
    public RecursiveParallelDFS(State state, Bound bound, DataStructures dataStructures, DFSListener dfsListener) {
        super(state, bound, dataStructures, dfsListener);
    }

    /**
     * Counts the number of active tasks left inside the ThreadPool
     */
    private static AtomicInteger _taskCount = new AtomicInteger(0);

    /**
     * runs dfs and everytime there is a free thread, a copy of the state is made and dfs
     * is run on the new thread from that position.
     * Otherwise, it will run as normal.
     * @param prevTask
     * @param prevProcessor
     */
    @Override
    protected void run(int prevTask, int prevProcessor) {
        _taskCount.incrementAndGet();
        // If there is "space" in the ThreadPool, queue the task in.
        if (_state._unassignedTasks >= (_state._numTasks/2) && _taskCount.get() < _numParallelCores) {

            // Increment count on queueing
            _taskCount.incrementAndGet();

            DFS dfs = new RecursiveParallelDFS(_state.copy(), _bound, _dataStructures, _dfsListener);
            _pool.submit(() -> {
                dfs.run(prevTask, prevProcessor);

                // Decrement count on completion
                _taskCount.decrementAndGet();
            });
        } else {
            super.run(prevTask, prevProcessor);
        }
        _taskCount.decrementAndGet();
    }

    /**
     * on dfs finish, blocks till there are no active tasks and shuts down the thread pool.
     */
    @Override
    protected void waitForFinish() {
        // Block until there are no active tasks.
        while (_taskCount.get() != 0) {
            // wait
        }
        _pool.shutdown();
        _pool = null;
    }

}
