package main.java.scheduler;

/**
 * An implementation of DFS where statistics are measured and updated in the InformationHolder instance.
 */
public class StatisticDFSListener extends MinimalDFSListener {
    public StatisticDFSListener(InformationHolder informationHolder) {
        super(informationHolder);
    }

    /**
     * On entry of dfs, increase the active branches and total states.
     */
    @Override
    public void onDFSEntry() {
        super.onDFSEntry();
        _informationHolder.incrementActiveBranches();
        _informationHolder.incrementTotalStates();
    }

    /**
     * at the end of dfs, decrease the active branches
     */
    @Override
    public void onDFSExit() {
        super.onDFSExit();
        _informationHolder.decrementActiveBranches();
    }

    /**
     * when there is a complete schedule, increase the number of complete states.
     * @param state
     * @param bound
     */
    @Override
    public void onCompleteSchedule(State state, Bound bound) {
        super.onCompleteSchedule(state, bound);
        _informationHolder.incrementCompleteStates();

    }

    /**
     * update the current state everytime it changes.
     * @param state
     * @param bound
     */
    @Override
    public void onPartialSchedule(State state, Bound bound) {
        super.onPartialSchedule(state, bound);
        _informationHolder.setCurrentState(state.copy());
    }
}
