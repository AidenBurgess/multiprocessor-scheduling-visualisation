package tests.java.dotio;

import main.java.dotio.Dependency;
import main.java.dotio.DotIO;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class DotIOTest {

    @Test
    public void readDot() {
        TaskGraph tg = DotIO.read(new StringReader(
                "digraph  \"example\" { a [Weight=2]; b [Weight=3]; a −> b [Weight=1]; c [Weight=3]; a −> c [Weight=2]; d [Weight=2]; b −> d [Weight=2]; c −> d [Weight=1];}"
        ));
        assertEquals("example", tg.getName());
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

        DotIO.write("testOutput.dot", taskGraph, startTimeMap, processorMap);

        File expected = new File("expectedOutput.dot");
        File actual = new File("testOutput.dot");

        // @todo create a line by line comparator, or convert to a string
//        assertEquals(expected, actual);
    }
}
