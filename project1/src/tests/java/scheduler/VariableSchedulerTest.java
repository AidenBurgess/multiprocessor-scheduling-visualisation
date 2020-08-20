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
    public void runSingleFile() {
        System.out.println(runTest("N11-M3.dot", 57));
    }

    long runTest(String testName, int answer) {
        try {
            TaskGraph tg = DotIO.read(System.getProperty("user.dir") + "/dots/" + testName);
            long totalTime = 0;
            long tests = 5;
            for (int i = 0; i < tests; i++) {
                Scheduler sc = new VariableScheduler(tg, 3, true);
                long executionTime = measureExecutionTime(sc);
                totalTime += executionTime;
                System.out.println("Time: " + executionTime);

                int bound = sc.getInformationHolder().getCurrentBound();
                assertEquals(answer, bound);
            }

            return totalTime / tests;

        } catch (DotIOException e) {
            e.printStackTrace();
            fail();
        } catch (FileNotFoundException e) {
            fail();
        }
        return -1;
    }

    private long measureExecutionTime(Scheduler sc) {
        long before = System.nanoTime();
        sc.execute();
        long after = System.nanoTime();
        return (after - before) / 1000000;
    }



}
