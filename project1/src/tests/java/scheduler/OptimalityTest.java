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
        File folder = new File("test-input");
        System.out.println(folder.getAbsoluteFile());
        for (final File file : folder.listFiles()) {
            TaskGraph taskGraph = DotIO.read("test-input/" + file.getName());
            String fileNameNoDot = file.getName().substring(0, file.getName().length() - 4);
            String lengthFileName = "test-result/" + fileNameNoDot + "-result.txt";
            File lengthFile = new File(lengthFileName);

            // Some condition, if you only want to test some inputs

            // if (!fileNameNoDot.contains("Nodes")) continue;

            int processors = 0, tens = 1;
            for (int i = fileNameNoDot.length() - 1; i >= 0; i--) {
                char c = fileNameNoDot.charAt(i);
                if (c >= '0' && c <= '9') {
                    processors += tens * (c - '0');
                    tens *= 10;
                } else {
                    break;
                }
            }

            try {
                Scanner scanner = new Scanner(lengthFile);
                long expectedLength = scanner.nextLong();
                params.add(new Object[]{taskGraph, file, processors, expectedLength});

            } catch (FileNotFoundException e) {
                // If file not found, ignore this case
            }
        }
        return params;
    }

    TaskGraph _taskGraph;
    File _file;
    int _processors;
    long _expected;

    public OptimalityTest(TaskGraph taskGraph, File file, int processors, long expected) {
        _taskGraph = taskGraph;
        _file = file;
        _processors = processors;
        _expected = expected;
    }

    @Test(timeout=200)
    public void test() {
        Scheduler scheduler = new VariableScheduler(_taskGraph, _processors,
                false, Config.SEQUENTIAL_EXECUTION);
        System.out.println(_file.getName());
        scheduler.execute();
        long actual = scheduler.getInformationHolder().getCurrentBound();

        assertEquals(_expected, actual);
    }
}
