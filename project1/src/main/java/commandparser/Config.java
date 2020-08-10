package main.java.commandparser;

public class Config {
    public int numProcessors;
    public String inputFileName;
    public boolean isParallelised;
    public int numParallelCores;
    public boolean hasVisualisation;
    public String outputFileName;

    @Override
    public String toString() {
        return "Config{" +
                "numProcessors=" + numProcessors +
                ", inputFileName='" + inputFileName + '\'' +
                ", isParallelised=" + isParallelised +
                ", numParallelCores=" + numParallelCores +
                ", hasVisualisation=" + hasVisualisation +
                ", outputFileName='" + outputFileName + '\'' +
                '}';
    }
}
