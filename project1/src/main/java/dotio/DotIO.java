package main.java.dotio;

import main.java.dotio.antlr.DOTLexer;
import main.java.dotio.antlr.DOTParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This static class handles the reading and writing of .dot syntax. It handles all the reading and writing of files,
 * as well as any syntax or error checking when it comes to the actual .dot file format.
 */
public class DotIO {

    /**
     * Reads in a file in the .dot format
     *
     * @note the input can have random spaces, so we will need to account for that.
     * digraph  "example" {
     *      a           [Weight=2];
     *      b           [Weight=3];
     *      a−> b       [Weight=1];
     *      c           [Weight=3];
     *      a−> c       [Weight=2];
     *      d           [Weight=2];
     *      b−> d       [Weight=2];
     *      c−> d       [Weight=1];
     * }
     *
     * @param inputFile The name of the file to be read.
     * @return The TaskGraph object
     * @throws DotIOException If there is an issue with reading the file, or an issue with the .dot syntax of the file.
     */
    public static TaskGraph read(String inputFile) throws DotIOException {
        TaskGraph graph = new TaskGraph();
        try {
            //Instantiate ANTLR lexer and parser, and parse through file with adapted dot listener.
            DOTLexer lexer = new DOTLexer(CharStreams.fromStream(new FileInputStream(inputFile)));
            DOTParser parser = new DOTParser(new CommonTokenStream(lexer));
            ParseTreeWalker.DEFAULT.walk(new AdaptedDotListener(graph), parser.graph());
        } catch (IOException e) {
            throw new DotIOException("Ensure that the file \"" + inputFile + "\" exists and is a valid text file.");
        }
        return graph;
    }

    /**
     * Takes in a task graph for the output.
     *
     *  digraph  "outputExample" {
     *      a           [Weight=2, Start=0, Processor=1];
     *      b           [Weight=3, Start=2, Processor=1];
     *      a −> b      [Weight=1];
     *      c           [Weight=3, Start=4, Processor=2];
     *      a −> c      [Weight=2];
     *      d           [Weight=2, Start=7, Processor=2];
     *      b −> d      [Weight=2];
     *      c −> d      [Weight=1];
     *  }
     *
     * @param outputFile
     * @param taskGraph
     * @param startTimeMap
     * @param processorMap
     *
     * @write to a .dot file
     */
    public static void write(String outputFile, TaskGraph taskGraph, HashMap<String, Integer> startTimeMap, HashMap<String, Integer> processorMap) throws DotIOException {

        // Use the task graph to get the ordering of the nodes and edges

        // for each node, check if it has a time in the map and which processor it has been assigned to.

        StringBuilder sb = new StringBuilder();

        try {
            PrintWriter writer = new PrintWriter(outputFile);

            // write the first line
            sb.append("digraph \"").append(taskGraph.getName()).append("\" {\n");

            // iterate through the task graph tasks
            ArrayList<Task> tasks = taskGraph.getTasks();

            for (Task task : tasks) {
                String taskName = task.getName();

                // if there is a start time
                if (startTimeMap.containsKey(taskName)) {

                    String taskTime = String.valueOf(task.getTaskTime());
                    String startTime = String.valueOf(startTimeMap.get(taskName));
                    String processor = String.valueOf(processorMap.get(taskName));
                    sb.append("\t").append(taskName).append("\t\t\t[Weight=").append(taskTime).append(", Start=").append(startTime).append(", Processor=").append(processor).append("];\n");
                } else {
                    throw new DotIOException("No valid schedule"); // ERROR: No valid schedule
                }
            }

            ArrayList<Dependency> dependencies = taskGraph.getDependencies();

            // add the rest of the dependencies
            for (Dependency dependency : dependencies) {

                String source = dependency.getSource();
                String dest = dependency.getDest();
                String communicationTime = String.valueOf(dependency.getCommunicationTime());

                sb.append("\t").append(source).append(" -> ").append(dest).append("\t\t[Weight=").append(communicationTime).append("];\n");
            }

            sb.append("}\n");

            writer.append(sb);

            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
