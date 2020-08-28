package main.java.commandparser;

import java.util.Objects;

/**
 * Objects of this class store the following information which is essential for running the program:
 * - the input file name
 * - the number of processors available to schedule the tasks on
 * - should visualization be shown
 * - should the execution of the program be parallelized, and if so how many parallel cores are available
 * - output file name
 */
public class Config {
    private int _numProcessors;
    private String _inputFileName;
    private String _outputFileName;

    public static final int SEQUENTIAL_EXECUTION = -1;

    // Optional parameters
    private boolean _isParallelised;
    private int _numParallelCores;
    private boolean _hasVisualisation;
    private boolean _checkValid;

    public Config() {
        // Default values
        _isParallelised = false;
        _numParallelCores = SEQUENTIAL_EXECUTION;
        _hasVisualisation = false;
        _checkValid = false;
    }

    // ------------------ Getters and Setters ------------------- //


    public int getNumProcessors() {
        return _numProcessors;
    }

    public void setNumProcessors(int numProcessors) {
        _numProcessors = numProcessors;
    }

    public String getInputFileName() {
        return _inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        _inputFileName = inputFileName;
    }
    
    public void setParallelised(boolean parallelised) {
        _isParallelised = parallelised;
    }

    public void setCheckValid(boolean checkValid) {
        _checkValid = checkValid;
    }

    public boolean isCheckValid() {
        return _checkValid;
    }

    public int getNumParallelCores() {
        return _numParallelCores;
    }

    public void setNumParallelCores(int numParallelCores) {
        _numParallelCores = numParallelCores;
    }

    public boolean hasVisualisation() {
        return _hasVisualisation;
    }

    public void setHasVisualisation(boolean hasVisualisation) {
        _hasVisualisation = hasVisualisation;
    }

    public String getOutputFileName() {
        return _outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        _outputFileName = outputFileName;
    }

    /**
     * Set the default name of output file to `${InputFileName}-output.dot`
     */
    public void setDefaultOutputFileName() {
        _outputFileName = _inputFileName.substring(0, _inputFileName.length() - 4).concat("-output.dot");
    }

    /**
     * Overriden to string to show the specific fields and values in the config
     * @return
     */
    @Override
    public String toString() {
        return "Config{" +
                "numProcessors=" + _numProcessors +
                ", inputFileName='" + _inputFileName + '\'' +
                ", isParallelised=" + _isParallelised +
                ", numParallelCores=" + _numParallelCores +
                ", hasVisualisation=" + _hasVisualisation +
                ", outputFileName='" + _outputFileName + '\'' +
                ", isCheckValid=" + _checkValid +
                '}';
    }

    /**
     * compares different configs with each other.
     * @param o other config object
     * @return boolean, true if they are identical, otherwise false.
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return _numProcessors == config._numProcessors &&
                _isParallelised == config._isParallelised &&
                _numParallelCores == config._numParallelCores &&
                _hasVisualisation == config._hasVisualisation &&
                _inputFileName.equals(config._inputFileName) &&
                _outputFileName.equals(config._outputFileName) &&
                (_checkValid == config._checkValid);
    }

    /**
     * Hashes the config class
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(_numProcessors, _inputFileName, _isParallelised, _numParallelCores, _hasVisualisation, _outputFileName);
    }
}
