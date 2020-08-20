package tests.java.scheduler;

import main.java.dotio.*;
import main.java.scheduler.Scheduler;
import main.java.scheduler.VariableScheduler;
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

            Scheduler sc = new VariableScheduler(tg, 3, false, 1);
            sc.execute();

            HashMap<String, Integer> startTimeMap = null; // = sc.getBestStartTimeMap();
            HashMap<String, Integer> processorMap = null; // = sc.getBestProcessorMap();
            for (String key : startTimeMap.keySet()) {
                System.out.println(key + " " + startTimeMap.get(key) + " " + processorMap.get(key));
            }

            DotIO.write("output.dot", tg, startTimeMap, processorMap);

        } catch (DotIOException e) {
            fail();
        } catch (FileNotFoundException e) {
            fail();
        }
    }



}
