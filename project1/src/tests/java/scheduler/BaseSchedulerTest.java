package tests.java.scheduler;

import main.java.dotio.*;

import main.java.scheduler.BaseScheduler;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;


import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.*;

public class BaseSchedulerTest {

    @Test
    public void run() {
        try {
            TaskGraph tg = DotIO.read(System.getProperty("user.dir") + "/src/tests/java/scheduler/testInput.dot");
            assertEquals("example", tg.getName());
            assertEquals(4, tg.getTasks().size());
            assertEquals(4, tg.getDependencies().size());

            BaseScheduler sc = new BaseScheduler(tg, 3);
            sc.execute();

            HashMap<String, Integer> startTimeMap = sc.getStartTimeMap();
            HashMap<String, Integer> processorMap = sc.getProcessorMap();
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
