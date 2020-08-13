package main.java.scheduler;

public class SimpleFFunction implements FFunction {

    @Override
    public int evaluate(Schedule s) {
        return s.endTime();
    }
}
