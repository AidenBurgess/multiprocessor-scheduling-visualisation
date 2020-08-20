package main.java.scheduler;

/**
 * This class is responsible for holding the DFS bound.
 * Any DFS instance can access this common object to determine whether to prune or not.
 */
public class Bound {
    public static final int NO_BOUND = -1;
    private int _bound = NO_BOUND;

    /**
     * Returns the current bound
     * @return bound
     */
    public int getBound() {
        return _bound;
    }

    /**
     * A boolean that determines if the DFS instance should prune or not.
     * Returns true if >= _bound, as the DFS will not provide a solution that is better than the bound.
     * @param currentBound bound that the current DFS is on.
     * @return true if the DFS should prune.
     */
    public boolean canPrune(int currentBound) {
        if (_bound == NO_BOUND) return false;
        return (currentBound >= _bound);
    }

    /**
     * Updates the bound value with currentBound IF currentBound is lower.
     * This ensures that the bound strictly decreases.
     * @param currentBound the value to update the bound to, if lower.
     */
    public void reduceBound(int currentBound) {
        if (_bound == NO_BOUND) _bound = currentBound;
        else _bound = Math.min(_bound, currentBound);
    }
}
