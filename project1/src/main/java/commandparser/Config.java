package main.java.commandparser;

import java.util.Objects;

public class Config {
    private int numProcessors;
    private String inputFileName;
    private boolean isParallelised;
    private int numParallelCores;
    private boolean hasVisualisation;
    private String outputFileName;

    public int getNumProcessors() {
        return numProcessors;
    }

    public void setNumProcessors(int numProcessors) {
        this.numProcessors = numProcessors;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public boolean isParallelised() {
        return isParallelised;
    }

    public void setParallelised(boolean parallelised) {
        isParallelised = parallelised;
    }

    public int getNumParallelCores() {
        return numParallelCores;
    }

    public void setNumParallelCores(int numParallelCores) {
        this.numParallelCores = numParallelCores;
    }

    public boolean hasVisualisation() {
        return hasVisualisation;
    }

    public void setHasVisualisation(boolean hasVisualisation) {
        this.hasVisualisation = hasVisualisation;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }



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

    public void setDefaultOutputFileName() {
        outputFileName = inputFileName.split(".dot")[0] + "-output.dot";
    }
}
