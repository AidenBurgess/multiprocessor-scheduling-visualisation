package tests.java.scheduler;

import main.java.commandparser.Config;
import main.java.dotio.*;
import main.java.scheduler.Scheduler;
import main.java.scheduler.VariableScheduler;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class VariableSchedulerTest {

    /**
     * Runs one specified file with varying statistics and parallel core numbers to compare performance
     * All output time is measured in ms.
     * @throws FileNotFoundException
     */
    @Test
    public void runSingleFile() throws FileNotFoundException {
        String testName = "N13-M3.dot";
        int expectedResult = 64;

        TaskGraph tg = DotIO.read(System.getProperty("user.dir") + "/test-input/" + testName);

        boolean[] stats = {false};
        int[] threads = {Config.SEQUENTIAL_EXECUTION, 2, 3, 3, 2, Config.SEQUENTIAL_EXECUTION};
        // Statistics, Processors
        for (boolean stat : stats) {
            for (int thread : threads) {
                long totalTime = 0;
                for (int tests = 0; tests < 3; tests++) {
                    Scheduler sc = new VariableScheduler(tg, 3, stat, thread);
                    totalTime += measureExecutionTime(sc);
                    assertEquals(sc.getInformationHolder().getCurrentBound(), expectedResult);
                }
                System.out.println(testName + ". Stats: " + stat + ", threads: " + thread + ". Time: " + totalTime/2);
            }
        }
    }

    private long measureExecutionTime(Scheduler sc) {
        long before = System.nanoTime();
        sc.execute();
        long after = System.nanoTime();
        return (after - before) / 1000000;
    }
}
