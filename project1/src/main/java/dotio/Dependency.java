package main.java.dotio;

public class Dependency {
    private String _source, _dest;
    private int _taskTime;

    // @todo constructor
    public Dependency(String source, String dest, int taskTime) {
        _source = source;
        _dest = dest;
        _taskTime = taskTime;
    }

    public String getSource() {
        return _source;
    }

    public String getDest() {
        return _dest;
    }

    public int getTaskTime() {
        return _taskTime;
    }
}
