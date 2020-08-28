package main.java.dotio;

/**
 * Shows the dependencies between two nodes and their communication time.
 */
public class Dependency {

    // source and destination nodes.
    private String _source, _dest;

    // communication time between processors
    private int _communicationTime;

    public Dependency(String source, String dest, int communicationTime) {
        _source = source;
        _dest = dest;
        _communicationTime = communicationTime;
    }

    // ------------------ Getters ------------------- //

    public String getSource() {
        return _source;
    }

    public String getDest() {
        return _dest;
    }

    public int getCommunicationTime() {
        return _communicationTime;
    }
}
