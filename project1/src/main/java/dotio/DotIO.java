package main.java.dotio;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Read a string of the file name and extract the contents to return a task graph.
 *
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
     * @param file
     * @return
     */
    public static TaskGraph read(String file) {

        return new TaskGraph();
    }

    /**
     * Takes in a task graph for the output.
     *
     *  digraph  "outputExample" {
     *      a           [Weight=2, Start=0, Processor=1];
     *      b           [Weight=3, Start=2, Processor=1];
     *      a−> b       [Weight=1];
     *      c           [Weight=3, Start=4, Processor=2];
     *      a−> c       [Weight=2];
     *      d           [Weight=2, Start=7, Processor=2];
     *      b−> d       [Weight=2];
     *      c−> d       [Weight=1];
     *  }
     *
     * Some writer example
     *  PrintWriter writer = new PrintWriter("prog5-grapha.dot");
     *                 writer.println("digraph program 5");
     *                 writer.println("{");
     *                 writer.println("a -> b -> c;");
     *                 writer.println("b -> d;");
     *                 writer.println("}");
     *
     *  writer.close();
     *
     * @param outputFile
     * @param taskGraph
     * @param startTimeMap
     * @param processorMap
     *
     * @write to a .dot file
     */
    public static void write(String outputFile, TaskGraph taskGraph, HashMap<String, Integer> startTimeMap, HashMap<String, Integer> processorMap) {

        // Use the task graph to get the ordering of the nodes and edges

        // for each node, check if it has a time in the map and which processor it has been assigned to.

        try {
            PrintWriter writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
