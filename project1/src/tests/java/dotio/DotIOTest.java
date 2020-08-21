package tests.java.dotio;

import main.java.dotio.*;

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;


import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.*;

public class DotIOTest {

    private static String testDir = System.getProperty("user.dir") + "/src/tests/java/dotio/";
    private static String dotExampleDir = System.getProperty("user.dir") + "/example-dots/";

    /**
     * Helper method developed to make testing files easier.
     * @param filename Name of dot file being tested
     * @param expectedName Expected name of graph in dot file
     * @param expectedTaskNo Expected number of tasks/nodes in dot file graph
     * @param expectedDependencyNo Expected number of dependencies/edges in dot file graph
     */
    private void testInputGraph(String filename, String expectedName, int expectedTaskNo, int expectedDependencyNo) {
        try {
            TaskGraph tg = DotIO.read(filename);
            assertEquals(expectedName, tg.getName());
            assertEquals(expectedTaskNo, tg.getTasks().size());
            assertEquals(expectedDependencyNo, tg.getDependencies().size());
        } catch (DotIOException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    public void readDot() {
        testInputGraph(testDir + "testInput.dot", "example", 4, 4);
    }

    @Test
    public void readNodes7OutTree() {
        testInputGraph(
                dotExampleDir + "Nodes_7_OutTree.dot",
                "OutTree-Balanced-MaxBf-3_Nodes_7_CCR_2.0_WeightType_Random",
                7,
                6
        );
    }

    @Test
    public void readNodes8Random() {
        testInputGraph(
                dotExampleDir + "Nodes_8_Random.dot",
                "Random_Nodes_8_Density_2.0_CCR_0.1_WeightType_Random",
                8,
                16
        );
    }

    @Test
    public void readNodes9SeriesParallel() {
        testInputGraph(
                dotExampleDir + "Nodes_9_SeriesParallel.dot",
                "SeriesParallel-MaxBf-3_Nodes_9_CCR_10.0_WeightType_Random",
                9,
                12
        );
    }

    @Test
    public void readNodes10Random() {
        testInputGraph(
                dotExampleDir + "Nodes_10_Random.dot",
                "Random_Nodes_10_Density_1.90_CCR_10.00_WeightType_Random",
                10,
                19
        );
    }

    @Test
    public void readNodes11OutTree() {
        testInputGraph(
                dotExampleDir + "Nodes_11_OutTree.dot",
                "OutTree-Balanced-MaxBf-3_Nodes_11_CCR_0.1_WeightType_Random",
                11,
                10
        );
    }

    @Test
    public void testNoWeightSpecified() {
        try {
            TaskGraph tg = DotIO.read(testDir + "noWeightInput.dot");
            fail();
        } catch (DotIOException e) {}
    }

    @Test
    public void testIllegalEdge() {
        try {
            TaskGraph tg = DotIO.read(testDir + "illegalEdgeInput.dot");
            fail();
        } catch (DotIOException e) {}
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

        try {
            DotIO.write(testDir + "/testOutput.dot", taskGraph, startTimeMap, processorMap);
        } catch (DotIOException e) {
            System.err.println(e.getMessage());
        }

        File expected = new File(testDir + "/expected.dot");
        File actual = new File(testDir + "/testOutput.dot");

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
