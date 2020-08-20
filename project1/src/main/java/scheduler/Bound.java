package main.java.scheduler;

public class Bound {
    public static final int NO_BOUND = -1;
    private int _bound = NO_BOUND;

    public int getBound() {
        return _bound;
    }

    public boolean canPrune(int otherBound) {
        if (_bound == NO_BOUND) return false;
        return (otherBound >= _bound);
    }
    public void reduceBound(int otherBound) {
        if (_bound == NO_BOUND) _bound = otherBound;
        else _bound = Math.min(_bound, otherBound);
        System.out.println(_bound);
    }
}
