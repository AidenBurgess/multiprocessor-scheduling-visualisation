package main.java.scheduler;

public interface Searcher {
    void optimalScheduleSearch(State state, DFSGetter dfsGetter);
}
