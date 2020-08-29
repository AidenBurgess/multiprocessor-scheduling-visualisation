package main.java.exception;

public class CommandParserException extends RuntimeException {
    public CommandParserException(String message) {
        super(message);
    }
}
