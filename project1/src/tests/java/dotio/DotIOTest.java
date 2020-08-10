package tests.java.dotio;

import main.java.dotio.DotIO;
import main.java.dotio.TaskGraph;

import org.junit.Test;
import org.junit.Assert;

import java.io.StringReader;

public class DotIOTest {

    @Test
    public void readDot() {
        TaskGraph tg = DotIO.read(new StringReader(
                "digraph  \"example\" { a [Weight=2]; b [Weight=3]; a −> b [Weight=1]; c [Weight=3]; a −> c [Weight=2]; d [Weight=2]; b −> d [Weight=2]; c −> d [Weight=1];}"
        ));
        Assert.assertEquals("example", tg.getName());
    }
}
