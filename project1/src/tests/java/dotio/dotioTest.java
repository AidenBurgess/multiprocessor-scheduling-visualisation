package tests.java.dotio;

import main.java.dotio.Dependency;
import main.java.dotio.DotIO;
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class dotioTest {

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

        ArrayList<Task> tasks = new ArrayList<Task>();
        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();

        // create a task graph

        // add some tasks
        tasks.add(new Task("a", 2));
        tasks.add(new Task("b", 3));
        tasks.add(new Task("c", 3));
        tasks.add(new Task("d", 2));

        // add some dependencies
        dependencies.add(new Dependency("a", "b", 1));
        dependencies.add(new Dependency("a", "c", 2));
        dependencies.add(new Dependency("b", "d", 2));
        dependencies.add(new Dependency("c", "d", 1));

        TaskGraph taskGraph = new TaskGraph("example", tasks, dependencies);

        DotIO.write("testOutput.dot", taskGraph, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        File expected = new File("expectedOutput.dot");
        File actual = new File("testOutput.dot");
    }
}
