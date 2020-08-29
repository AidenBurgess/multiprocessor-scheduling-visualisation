package tests.java.validitychecker;

import main.java.dotio.Dependency;
import main.java.dotio.Task;
import main.java.exception.ValidityCheckerException;
import main.java.validitychecker.ValidityChecker;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Responsible for testing the correctness of the ValidityChecker.
 */
public class ValidityCheckerTest {
    ValidityChecker validityChecker;
    private ArrayList<Task> _tasks;
    private ArrayList<Dependency> _dependencies;
    private HashMap<String, Integer> _bestStartTimeMap;
    private HashMap<String, Integer> _bestProcessorMap;

    @Before
    public void initialise() {
        _tasks = new ArrayList<>();
        _dependencies = new ArrayList<>();
        _bestStartTimeMap = new HashMap<>();
        _bestProcessorMap = new HashMap<>();
    }

    /**
     * Test simple case with one processor
     */
    @Test
    public void testWithValidScheduleOnOneProcessor() {
        _tasks.add(new Task("A", 2));
        _tasks.add(new Task("B", 3));

        _dependencies.add(new Dependency("A", "B", 1));

        _bestProcessorMap.put("A", 1);
        _bestProcessorMap.put("B", 1);

        _bestStartTimeMap.put("A", 0);
        _bestStartTimeMap.put("B", 2);

        validityChecker = new ValidityChecker(_tasks, _dependencies, _bestProcessorMap, _bestStartTimeMap);

        validityChecker.check();
    }

    /**
     * Tests simple case with two processors
     */
    @Test
    public void testWithValidScheduleOnTwoProcessors() {
        _tasks.add(new Task("A", 2));
        _tasks.add(new Task("B", 3));

        _dependencies.add(new Dependency("A", "B", 1));

        _bestProcessorMap.put("A", 1);
        _bestProcessorMap.put("B", 2);

        _bestStartTimeMap.put("A", 0);
        _bestStartTimeMap.put("B", 3);

        validityChecker = new ValidityChecker(_tasks, _dependencies, _bestProcessorMap, _bestStartTimeMap);

        validityChecker.check();
    }

    /**
     * Tests when dependency is not satisfied on one processor
     */
    @Test
    public void testWithInvalidScheduleOnOneProcessor() {
        _tasks.add(new Task("A", 2));
        _tasks.add(new Task("B", 3));

        _dependencies.add(new Dependency("A", "B", 1));

        _bestProcessorMap.put("A", 1);
        _bestProcessorMap.put("B", 1);

        _bestStartTimeMap.put("A", 0);
        _bestStartTimeMap.put("B", 1);

        validityChecker = new ValidityChecker(_tasks, _dependencies, _bestProcessorMap, _bestStartTimeMap);

        try {
            validityChecker.check();
            fail();
        } catch (ValidityCheckerException e) {

        }
    }

    /**
     * Tests when depedency is not satisfied on two processors
     */
    @Test
    public void testWithInvalidScheduleOnTwoProcessor() {
        _tasks.add(new Task("A", 2));
        _tasks.add(new Task("B", 3));

        _dependencies.add(new Dependency("A", "B", 1));

        _bestProcessorMap.put("A", 1);
        _bestProcessorMap.put("B", 2);

        _bestStartTimeMap.put("A", 0);
        _bestStartTimeMap.put("B", 1);

        validityChecker = new ValidityChecker(_tasks, _dependencies, _bestProcessorMap, _bestStartTimeMap);

        try {
            validityChecker.check();
            fail();
        } catch (ValidityCheckerException e) {

        }
    }

    /**
     * Tests when tasks are overlapping on the same processor
     */
    @Test
    public void testWithSameScheduledTaskOnSameProcessor() {
        _tasks.add(new Task("A", 2));
        _tasks.add(new Task("B", 2));

        _dependencies.add(new Dependency("A", "B", 2));

        _bestProcessorMap.put("A", 1);
        _bestProcessorMap.put("B", 1);

        _bestStartTimeMap.put("A", 0);
        _bestStartTimeMap.put("B", 0);

        validityChecker = new ValidityChecker(_tasks, _dependencies, _bestProcessorMap, _bestStartTimeMap);

        try {
            validityChecker.check();
            fail();
        } catch (ValidityCheckerException e) {

        }
    }

    /**
     * Tests when tasks are bordering on the same processor. Expect pass
     */
    @Test
    public void testWithBorderingTasksOnSameProcessor() {
        _tasks.add(new Task("A", 2));
        _tasks.add(new Task("B", 2));

        _bestProcessorMap.put("A", 1);
        _bestProcessorMap.put("B", 1);

        _bestStartTimeMap.put("A", 2);
        _bestStartTimeMap.put("B", 0);

        validityChecker = new ValidityChecker(_tasks, _dependencies, _bestProcessorMap, _bestStartTimeMap);
        validityChecker.check();
    }

}
