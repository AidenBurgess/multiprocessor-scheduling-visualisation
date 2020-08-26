package main.java.scheduler;

public class MinimalDFSGetter extends DFSGetter {
    public MinimalDFSGetter(Bound bound, DataStructures dataStructures, InformationHolder informationHolder) {
        super(bound, dataStructures, informationHolder);
    }

    @Override
    protected DFS getDFSInstance(State state) {
        return new MinimalDFS(state, _bound, _dataStructures, _informationHolder);
    }
}
