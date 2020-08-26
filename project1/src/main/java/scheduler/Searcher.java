package main.java.scheduler;

public interface Searcher {
    void optimalScheduleSearch(State state, Bound bound, DataStructures dataStructures,
                               InformationHolder informationHolder, DFSListener dfsListener);
}
