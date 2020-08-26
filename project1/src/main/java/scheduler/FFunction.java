package main.java.scheduler;

public class FFunction {

    /**
     * Returns the minimum time the current State will take to complete.
     * Uses load balancing to calculate the best expected time if all tasks were shared perfectly.
     * @param state State to determine the FFunction value
     * @return An estimation of the minimum time required for state to complete.
     */
    public static int evaluate(State state) {
        int loadBalancedTime = (int)Math.ceil((state._computationalTime)/(double)state._numProcessors);

        return Math.max(loadBalancedTime, state._endTime);
    }
}
