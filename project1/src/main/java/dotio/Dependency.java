package main.java.dotio;

public class Dependency {
    private String _source, _dest;
    private int _communicationTime;

    public Dependency(String source, String dest, int communicationTime) {
        _source = source;
        _dest = dest;
        _communicationTime = communicationTime;
    }

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
