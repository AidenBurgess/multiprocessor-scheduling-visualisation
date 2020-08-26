package main.java.scheduler;

public class FFunction {

    public static int evaluate(State state) {
        int idleTime = (int)Math.ceil((state._computationalTime)/(double)state._numProcessors);
//        int criticalPathBound = bound, dataReadyTime;

        return Math.max(idleTime, state._endTime);

    }
}
