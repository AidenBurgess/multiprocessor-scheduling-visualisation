package main.java.commandparser;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return numProcessors == config.numProcessors &&
                isParallelised == config.isParallelised &&
                numParallelCores == config.numParallelCores &&
                hasVisualisation == config.hasVisualisation &&
                inputFileName.equals(config.inputFileName) &&
                outputFileName.equals(config.outputFileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numProcessors, inputFileName, isParallelised, numParallelCores, hasVisualisation, outputFileName);
    }
}
