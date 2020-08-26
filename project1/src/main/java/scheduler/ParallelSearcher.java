package main.java.scheduler;

import javafx.util.Pair;
import sun.awt.image.ImageWatched;
import sun.plugin.javascript.navig.Link;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelSearcher implements Searcher {
    ExecutorService _pool;
    int _numParallelCores;

    public ParallelSearcher(int numParallelCores) {
        _numParallelCores = numParallelCores;
        _pool = Executors.newFixedThreadPool(numParallelCores);
    }

    @Override
    public void optimalScheduleSearch(State state, Bound bound, DataStructures dataStructures,
                                      InformationHolder informationHolder, DFSListener dfsListener) {
        DFS dfs = new DFS(state, bound, dataStructures, informationHolder, dfsListener);
        dfs.run();

        _pool.shutdown();
        try {
            _pool.awaitTermination(1000, TimeUnit.SECONDS); // todo timeout?
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
