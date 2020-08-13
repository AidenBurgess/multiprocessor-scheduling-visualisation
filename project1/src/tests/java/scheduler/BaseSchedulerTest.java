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
            TaskGraph tg = DotIO.read(new StringReader(
                    "digraph  \"example\" {\n\ta [Weight=2];\n\tb [Weight=3];\n\ta −> b [Weight=1];\n\tc [Weight=3];\n\ta −> c [Weight=2];\n\td [Weight=2];\n\tb −> d [Weight=2];\n\tc −> d [Weight=1];\n}"
            ));
            assertEquals("example", tg.getName());
            assertEquals(4, tg.getTasks().size());
            assertEquals(4, tg.getDependencies().size());

            BaseScheduler sc = new BaseScheduler(tg, 2);
            sc.execute();

            HashMap<String, Integer> startTimeMap = sc.getStartTimeMap();
            HashMap<String, Integer> processorMap = sc.getProcessorMap();
            for (String key : startTimeMap.keySet()) {
                System.out.println(key + " " + startTimeMap.get(key) + " " + processorMap.get(key));
            }

        } catch (DotIOException e) {
            fail();
        }
    }


}
