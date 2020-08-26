package main.java.scheduler;

public class SequentialSearcher implements Searcher {
    @Override
    public void optimalScheduleSearch(State state, Bound bound, DataStructures dataStructures,
                                      InformationHolder informationHolder, DFSListener dfsListener) {
        DFS dfs = new DFS(state, bound, dataStructures, informationHolder, dfsListener);
        dfs.run();
    }
}
