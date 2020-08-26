package main.java.scheduler;

public abstract class DFSGetter {
    Bound _bound;
    DataStructures _dataStructures;
    InformationHolder _informationHolder;

    protected DFSGetter(Bound bound, DataStructures dataStructures, InformationHolder informationHolder) {
        _bound = bound;
        _dataStructures = dataStructures;
        _informationHolder = informationHolder;
    }

    protected abstract DFS getDFSInstance(State state);
}
