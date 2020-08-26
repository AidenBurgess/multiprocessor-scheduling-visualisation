package main.java.scheduler;

public class StatisticDFSGetter extends DFSGetter {
    public StatisticDFSGetter(Bound bound, DataStructures dataStructures, InformationHolder informationHolder) {
        super(bound, dataStructures, informationHolder);
    }

    @Override
    protected DFS getDFSInstance(State state) {
        return new StatisticDFS(state, _bound, _dataStructures, _informationHolder);
    }
}
