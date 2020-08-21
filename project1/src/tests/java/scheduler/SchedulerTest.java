package tests.java.scheduler;

import main.java.dotio.*;
import main.java.scheduler.ReducedStateScheduler;
import main.java.scheduler.Scheduler;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.*;

public class SchedulerTest {

    @Test
    public void run() {
        try {
            TaskGraph tg = DotIO.read(System.getProperty("user.dir") + "/src/tests/java/scheduler/testInput.dot");
            assertEquals("example", tg.getName());
            assertEquals(4, tg.getTasks().size());
            assertEquals(4, tg.getDependencies().size());

            Scheduler sc = new ReducedStateScheduler(tg, 3);
            sc.execute();

            HashMap<String, Integer> startTimeMap = sc.getBestStartTimeMap();
            HashMap<String, Integer> processorMap = sc.getBestProcessorMap();
            for (String key : startTimeMap.keySet()) {
                System.out.println(key + " " + startTimeMap.get(key) + " " + processorMap.get(key));
            }

            DotIO.write("output.dot", tg, startTimeMap, processorMap);

        } catch (DotIOException e) {
            fail();
        }
    }



}
