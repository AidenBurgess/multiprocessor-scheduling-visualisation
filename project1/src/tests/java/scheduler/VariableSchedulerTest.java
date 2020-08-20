package tests.java.scheduler;

import main.java.dotio.*;
import main.java.scheduler.Scheduler;
import main.java.scheduler.VariableScheduler;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.*;

public class VariableSchedulerTest {

    @Test
    public void runSingleFile() throws FileNotFoundException, DotIOException {
        String testName = "N11-M1.dot";
        int expectedResult = 54;

        TaskGraph tg = DotIO.read(System.getProperty("user.dir") + "/dots/" + testName);
        // Statistics, Processors
        for (boolean stats = false; ; stats = true) {
            for (int threads = 3; threads >= 1; threads--) {
                long totalTime = 0;
                for (int tests = 0; tests < 3; tests++) {
                    Scheduler sc = new VariableScheduler(tg, 3, stats, threads);
                    totalTime += measureExecutionTime(sc);
                    assertEquals(sc.getInformationHolder().getCurrentBound(), expectedResult);
                }
                System.out.println(testName + ". Stats: " + stats + ", threads: " + threads + ". Time: " + totalTime/3);
            }

            if (stats == true) break;
        }


    }

    private long measureExecutionTime(Scheduler sc) {
        long before = System.nanoTime();
        sc.execute();
        long after = System.nanoTime();
        return (after - before) / 1000000;
    }



}
