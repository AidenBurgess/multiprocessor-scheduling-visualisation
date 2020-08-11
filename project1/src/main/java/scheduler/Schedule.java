package main.java.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Schedule {
    private List<Processor> processors;
    private int numProcessors;

    public Schedule(int numProcessors) {
        processors = new ArrayList<>();
        this.numProcessors = numProcessors;

        for(int i = 0; i < numProcessors; i++){
            processors.add(new Processor(i));
        }
    }
}
