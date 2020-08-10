package tests.java.commandparser;

import main.java.commandparser.CommandParser;
import main.java.commandparser.Config;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommandParserTest {

    @Test
    public void simpleParse() {
        String[] simpleArgs = {"test-input.dot", "5"};
        Config expectedConfig = new Config();
        expectedConfig.inputFileName = "test-input.dot";
        expectedConfig.numProcessors = 5;
        expectedConfig.outputFileName = "test-input-output.dot";
        expectedConfig.isParallelised = false;
        expectedConfig.hasVisualisation = false;
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

    @Test
    public void hasVisualisationParse() {
        String[] simpleArgs = {"visualisation.dot", "10", "-v"};
        Config expectedConfig = new Config();
        expectedConfig.inputFileName = "visualisation.dot";
        expectedConfig.numProcessors = 10;
        expectedConfig.outputFileName = "visualisation-output.dot";
        expectedConfig.hasVisualisation = true;
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

    @Test
    public void outputFileParse() {
        String[] simpleArgs = {"output.dot", "10", "-o", "different.dot"};
        Config expectedConfig = new Config();
        expectedConfig.inputFileName = "output.dot";
        expectedConfig.numProcessors = 10;
        expectedConfig.outputFileName = "different.dot";
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

    @Test
    public void parallelParse() {
        String[] simpleArgs = {"parallel.dot", "10", "-p", "4"};
        Config expectedConfig = new Config();
        expectedConfig.inputFileName = "parallel.dot";
        expectedConfig.numProcessors = 10;
        expectedConfig.numParallelCores = 4;
        expectedConfig.isParallelised = true;
        expectedConfig.outputFileName = "parallel-output.dot";
        Config resConfig = CommandParser.parse(simpleArgs);
        assertEquals(expectedConfig, resConfig);
    }

}
