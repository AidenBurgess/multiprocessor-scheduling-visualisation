package tests.java.commandparser;

import main.java.commandparser.CommandParser;
import main.java.commandparser.Config;
import main.java.exception.CommandParserException;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommandParserTest {

    /**
     * Tests correctness of a minimal command input
     */
    @Test
    public void simpleParse() {
        String[] simpleArgs = {"test-input.dot", "5"};
        Config expectedConfig = new Config();
        expectedConfig.setInputFileName("test-input.dot");
        expectedConfig.setNumProcessors(5);
        expectedConfig.setOutputFileName("test-input-output.dot");
        expectedConfig.setHasVisualisation(false);
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

    /**
     * Test visualistaion tag -v is read correctly
     */
    @Test
    public void hasVisualisationParse() {
        String[] simpleArgs = {"visualisation.dot", "10", "-v"};
        Config expectedConfig = new Config();
        expectedConfig.setInputFileName("visualisation.dot");
        expectedConfig.setNumProcessors(10);
        expectedConfig.setOutputFileName("visualisation-output.dot");
        expectedConfig.setHasVisualisation(true);
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

    /**
     * Tests output file tag -o is read correctly
     */
    @Test
    public void outputFileParse() {
        String[] simpleArgs = {"output.dot", "10", "-o", "different.dot"};
        Config expectedConfig = new Config();
        expectedConfig.setInputFileName("output.dot");
        expectedConfig.setNumProcessors(10);
        expectedConfig.setOutputFileName("different.dot");
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

    /**
     * Tests the number of parallel cores -p is read correctly
     */
    @Test
    public void parallelParse() {
        String[] simpleArgs = {"parallel.dot", "10", "-p", "4"};
        Config expectedConfig = new Config();
        expectedConfig.setInputFileName("parallel.dot");
        expectedConfig.setNumProcessors(10);
        expectedConfig.setNumParallelCores(4);
        expectedConfig.setOutputFileName("parallel-output.dot");
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

    /**
     * Tests all the combinations of inputs
     */
    @Test
    public void allOptionsParse() {
        String[] simpleArgs = {"allOptions.dot", "5", "-o", "none.dot", "-v", "-p", "20"};
        Config expectedConfig = new Config();
        expectedConfig.setInputFileName("allOptions.dot");
        expectedConfig.setNumProcessors(5);
        expectedConfig.setNumParallelCores(20);
        expectedConfig.setHasVisualisation(true);
        expectedConfig.setOutputFileName("none.dot");
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

    /**
     * Tests the optional arguments (-p, -v, -o) in scrambled order
     */
    @Test
    public void allOptionsScrambledParse() {
        String[] simpleArgs = {"allOptions.dot", "5", "-v", "-p", "15", "-o", "nothing.dot"};
        Config expectedConfig = new Config();
        expectedConfig.setInputFileName("allOptions.dot");
        expectedConfig.setNumProcessors(5);
        expectedConfig.setNumParallelCores(15);
        expectedConfig.setHasVisualisation(true);
        expectedConfig.setOutputFileName("nothing.dot");
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

    /**
     * Tests --help interrupts the code
     */
    @Test
    public void help() {
        String[] simpleArgs = {"--help"};
        try {
            CommandParser.parse(simpleArgs);
            fail();
        } catch (CommandParserException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests incorrect processors interrupts the code
     */
    @Test
    public void invalidProcessorNumber() {
        String[] simpleArgs = {"filename.dot", "not-a-number"};
        try {
            CommandParser.parse(simpleArgs);
            fail();
        } catch (CommandParserException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests incorrect number of parallel cores interrupts the code
     */
    @Test
    public void invalidParallelCoresNumber() {
        String[] simpleArgs = {"filename.dot", "3", "-p", "a"};
        try {
            CommandParser.parse(simpleArgs);
            fail();
        } catch (CommandParserException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests invalid options are picked up
     */
    @Test
    public void invalidOption() {
        String[] simpleArgs = {"filename.dot", "3", "-hello"};
        try {
            CommandParser.parse(simpleArgs);
            fail();
        } catch (CommandParserException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests when output file is not provided, code is interrupted
     */
    @Test
    public void noFileSpecifiedAfterOutput() {
        String[] simpleArgs = {"filename.dot", "3", "-o"};
        try {
            CommandParser.parse(simpleArgs);
            fail();
        } catch (CommandParserException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests when num parallel cores is not provided, code is interrupted
     */
    @Test
    public void numParallelCoresNotSpecified() {
        String[] simpleArgs = {"filename.dot", "3", "-p"};
        try {
            CommandParser.parse(simpleArgs);
            fail();
        } catch (CommandParserException e) {
            System.out.println(e.getMessage());
        }
    }
}
