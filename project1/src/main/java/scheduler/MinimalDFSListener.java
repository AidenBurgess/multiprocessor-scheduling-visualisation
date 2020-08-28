package main.java.scheduler;

/**
 * Provides a minimal implementation of DFSListener.
 * Only responsible for updating the best found state.
 */
public class MinimalDFSListener implements DFSListener {
    protected InformationHolder _informationHolder;

    public MinimalDFSListener(InformationHolder informationHolder) {
        _informationHolder = informationHolder;
    }

    /**
     * Does nothing if it is a minimal dfs (no visualisation)
     */
    @Override
    public void onDFSEntry() {
        // Do nothing
    }

    /**
     * Does nothing if it is a minimal dfs (no visualisation)
     */
    @Override
    public void onDFSExit() {
        // Do nothing
    }

    /**
     * Update the best state and bound when there is a complete state.
     * @param state
     * @param bound
     */
    @Override
    public void onCompleteSchedule(State state, Bound bound) {
        // Update the best state
        _informationHolder.setBestState(state.copy());
        _informationHolder.setCurrentBound(bound.getBound());
    }

    /**
     * Don't do anything if it isn't finished.
     * @param state
     * @param bound
     */
    @Override
    public void onPartialSchedule(State state, Bound bound) {
        // Do nothing
    }
}
