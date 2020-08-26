package main.java.scheduler;

public class FFunction {

    /**
     * uses load balancing to calculate the best expected time if all tasks were shared perfectly.
     * @param state
     * @return int, the max value between the load balanced time and current end time.
     */
    public static int evaluate(State state) {

        int loadBalancedTime = (int)Math.ceil((state._computationalTime)/(double)state._numProcessors);

        return Math.max(loadBalancedTime, state._endTime);
    }
}
