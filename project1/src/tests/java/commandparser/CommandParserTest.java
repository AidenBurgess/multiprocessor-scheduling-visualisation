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

}
