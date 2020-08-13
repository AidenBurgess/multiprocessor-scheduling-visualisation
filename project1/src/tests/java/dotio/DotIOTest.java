package tests.java.dotio;

import main.java.dotio.*;

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;


import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.*;

public class DotIOTest {

    private String getTestDir() {
        return System.getProperty("user.dir") + "/src/tests/java/dotio/";
    }

    @Test
    public void readDot() {
        try {
            TaskGraph tg = DotIO.read(getTestDir() + "testInput.dot");
            assertEquals("example", tg.getName());
            assertEquals(4, tg.getTasks().size());
            assertEquals(4, tg.getDependencies().size());
        } catch (DotIOException e) {
            fail("Failed to read syntax: " + e.getMessage());
        } catch (FileNotFoundException e) {
            fail("Failed to locate file");
        }
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /*      a           [Weight=2];
     *      b           [Weight=3];
     *      a−> b       [Weight=1];
     *      c           [Weight=3];
     *      a−> c       [Weight=2];
     *      d           [Weight=2];
     *      b−> d       [Weight=2];
     *      c−> d       [Weight=1];
     */
    @Test
    public void testWrite() {
        TaskGraph taskGraph = new TaskGraph("example");
        HashMap<String, Integer> startTimeMap = new HashMap<String, Integer>();
        HashMap<String, Integer> processorMap = new HashMap<String, Integer>();

        startTimeMap.put("a", 0);
        startTimeMap.put("b", 2);
        startTimeMap.put("c", 4);
        startTimeMap.put("d", 7);

        processorMap.put("a",1);
        processorMap.put("b",1);
        processorMap.put("c",2);
        processorMap.put("d",2);

        // create a task graph
        // add some tasks
        taskGraph.insertTask(new Task("a", 2));
        taskGraph.insertTask(new Task("b", 3));
        taskGraph.insertTask(new Task("c", 3));
        taskGraph.insertTask(new Task("d", 2));

        // add some dependencies
        taskGraph.insertDependency(new Dependency("a", "b", 1));
        taskGraph.insertDependency(new Dependency("a", "c", 2));
        taskGraph.insertDependency(new Dependency("b", "d", 2));
        taskGraph.insertDependency(new Dependency("c", "d", 1));

        // get the current dotio directory
        String currentDirectory = System.getProperty("user.dir") + "/src/tests/java/dotio";

        try {
            DotIO.write(currentDirectory + "/testOutput.dot", taskGraph, startTimeMap, processorMap);
        } catch (DotIOException e) {
            System.err.println(e.getMessage());
        }

        File expected = new File(currentDirectory + "/expected.dot");
        File actual = new File(currentDirectory + "/testOutput.dot");

        try {
            // reads the expected file
            FileReader frExpected = new FileReader(expected);

            // creates the buffering character input stream
            BufferedReader brExpected = new BufferedReader(frExpected);

            // reads the actual file
            FileReader frActual = new FileReader(actual);

            // creates the buffering character input stream
            BufferedReader brActual = new BufferedReader(frActual);

            String lineExpected;

            // check each line and if they differ at any point, it will fail
            while ((lineExpected = brExpected.readLine()) != null) {
                String lineActual = brActual.readLine();

                System.out.println(lineActual);

                assertEquals(lineExpected, lineActual);
            }

            // if there is still some content in either file, fail
            if (brExpected.readLine() != null || brActual.readLine() != null) {
                fail();
            }

            // closes the streams and release the resources
            frExpected.close();
            frActual.close();
        } catch(IOException e) {

            e.printStackTrace();
        }
    }
}
