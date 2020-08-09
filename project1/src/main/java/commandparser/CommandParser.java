package main.java.commandparser;

/**
 * Take a string args from main and will read the number of processors, whether to parallise
 * and will be try or false.
 */
public class CommandParser {
    public static Config parse(String[] args) {
        System.out.println("Command has been parsed!");
        return new Config();
    }

}
