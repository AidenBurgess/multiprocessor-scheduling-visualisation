package tests.java.scheduler;

import main.java.dotio.*;
import main.java.scheduler.Scheduler;
import main.java.scheduler.VariableScheduler;
import main.java.validitychecker.ValidityChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Responsible to testing that the results are correct and optimum.
 */
@RunWith(Parameterized.class)
public class OptimalityTest {
    /**
     * This method allows tests to be parameterised - we can test over multiple files without writing
     * one JUnit test for each file
     *
     * This method is called first on running "OptimalityTest".
     * This method "initialises" all the input files and sets up a Collection of Object[]
     * Each Object[] is the parameters to the OptimalityTest constructor below
     * @return a Collection of Object[] where each Object[] is the parameters to the constructor
     */
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> params = new ArrayList<>();
        File folder = new File("test-input");
        System.out.println(folder.getAbsoluteFile());

        // Retrieves all the files from the "test-input" folder
        for (final File file : folder.listFiles()) {
            // Opens the file and the expected result file
            TaskGraph taskGraph = DotIO.read("test-input/" + file.getName());
            String fileNameNoDot = file.getName().substring(0, file.getName().length() - 4);
            String lengthFileName = "test-result/" + fileNameNoDot + "-result.txt";
            File lengthFile = new File(lengthFileName);

            // ignore all tests which are 16, 20, or 22 nodes.
            String[] ignore = {"16", "20", "22"};
            boolean ok = true;
            for (String ig : ignore) {
                if (fileNameNoDot.contains(ig)) ok = false;
            }
            if (!ok) continue;

            // The file format is FileName-P5.dot
            // This method parses the number of processors of this input.
            // E.g, the above file would produce processors = 5
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

            // Reads the contents of the expected length file
            try {
                Scanner scanner = new Scanner(lengthFile);
                long expectedLength = scanner.nextLong();

                // Creates one Object[]. This will be passed into the constructor when tests run.
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

    /**
     * Constructor - takes in the Object[] parameters defined in the method above
     */
    public OptimalityTest(TaskGraph taskGraph, File file, int processors, long expected) {
        _taskGraph = taskGraph;
        _file = file;
        _processors = processors;
        _expected = expected;
    }

    /**
     * This method is called automatically once per test case.
     */
    @Test(timeout=1000)
    public void test() {
        Scheduler scheduler = new VariableScheduler(_taskGraph, _processors,
                false, 3);
        System.out.println(_file.getName());
        scheduler.execute();
        long actual = scheduler.getInformationHolder().getCurrentBound();

        ValidityChecker validityChecker = new ValidityChecker(
                _taskGraph.getTasks(),
                _taskGraph.getDependencies(),
                scheduler.getInformationHolder().getScheduleStateMaps().getBestProcessorMap(),
                scheduler.getInformationHolder().getScheduleStateMaps().getBestStartTimeMap());

        validityChecker.check(); // Check that the output is valid
        assertEquals(_expected, actual); // Check that the output is optimal
    }
}
