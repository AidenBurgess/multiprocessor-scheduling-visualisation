package main.java.dotio;

public class Task {
    private String _name;
    private int _communicationTime;

    public Task(String name, int communicationTime) {
        _name = name;
        _communicationTime = communicationTime;
    }

    public String getName() {
        return _name;
    }

    public int getCommunicationTime() {
        return _communicationTime;
    }
}
