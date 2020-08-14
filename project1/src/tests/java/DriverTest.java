package tests.java;

import main.java.Driver;
import org.junit.Test;

public class DriverTest {

    @Test
    public void main() {
    }

    @Test
    public void startVisualisationThread() {
    }

    @Test (timeout = 20000)
    public void runtimeTesting() {
        System.out.println("Filename\t\t\tTimes are given in ms");
        int maxProcessors = 5;
        int maxTasks = 10;
        for (int N = 1; N < maxTasks+1; N++) {
            int maxDependencies = (N*(N-1))/2 + 1;

            for (int M = 0; M < maxDependencies; M++) {
                String filename = String.format("N%d-M%d.dot", N, M);
                System.out.format("%10s", filename);
                for (int P = 1; P < maxProcessors+1; P++) {
                    String[] input = {"dots/" + filename, Integer.toString(P)};
                    long startTime = System.nanoTime();
                    Driver.main(input);
                    long duration = (System.nanoTime() - startTime)/1000000;
                    System.out.format("%10d", duration);
                }
                System.out.println();
            }
        }
    }
}