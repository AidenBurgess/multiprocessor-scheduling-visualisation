package tests.java.scheduler;

import main.java.commandparser.Config;
import main.java.dotio.*;
import main.java.scheduler.Scheduler;
import main.java.scheduler.VariableScheduler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OptimalityTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> params = new ArrayList<>();
        File folder = new File("input-dots");
        System.out.println(folder.getAbsoluteFile());
        for (final File file : folder.listFiles()) {
            TaskGraph taskGraph = DotIO.read("input-dots/" + file.getName());

            for (int p = 1; p <= 5; p++) {
                params.add(new Object[]{taskGraph, file, p});
            }
        }
        return params;
    }

    TaskGraph _taskGraph;
    File _file;
    int _p;


    public OptimalityTest(TaskGraph taskGraph, File file, int p) {
        _taskGraph = taskGraph;
        _file = file;
        _p = p;
    }

    @Test(timeout=5000)
    public void test() {
        Scheduler scheduler = new VariableScheduler(_taskGraph, _p, false, Config.SEQUENTIAL_EXECUTION);
        scheduler.execute();
        long actualLength = scheduler.getInformationHolder().getCurrentBound();

        String fileNameNoDot = _file.getName().substring(0, _file.getName().length() - 4);
        String lengthFileName = "output-dots/" + fileNameNoDot + "-P" + _p + "-length.txt";
        File lengthFile = new File(lengthFileName);

        try {
            Scanner scanner = new Scanner(lengthFile);
            long expectedLength = scanner.nextLong();
            assertEquals(expectedLength, actualLength);
        } catch (FileNotFoundException e) {
            System.out.println("I found a solution but he didn't?");
        }
    }






}
