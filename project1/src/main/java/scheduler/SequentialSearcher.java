package main.java.scheduler;

public class SequentialSearcher implements Searcher {
    @Override
    public void optimalScheduleSearch(State state, DFSGetter dfsGetter) {
        DFS dfs = dfsGetter.getDFSInstance(state);
        dfs.run();
    }
}
