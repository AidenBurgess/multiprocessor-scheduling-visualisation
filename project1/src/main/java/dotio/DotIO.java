package main.java.dotio;

import main.java.dotio.antlr.DOTLexer;
import main.java.dotio.antlr.DOTParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
     * @param inputFile The name of the file to be read.
     * @return
     */
    public static TaskGraph read(String inputFile) throws DotIOException, FileNotFoundException {
        TaskGraph graph = new TaskGraph();
        try {

            DOTLexer lexer = new DOTLexer(new ANTLRInputStream(new FileInputStream(inputFile)));
            DOTParser parser = new DOTParser(new CommonTokenStream(lexer));
            ParseTree tree = parser.graph();
            ParseTreeWalker.DEFAULT.walk(new AdaptedDotListener(graph), tree);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return graph;
    }

    /**
     * Read an individual graph object (i.e. a node or an edge) from a dot file and add it to the specified graph object
     * @param tk The StreamTokenizer object being used to parse the input file.
     * @param graph The TaskGraph object being created to encapsulate the graph from the .dot file.
     * @throws IOException If an IOException is thrown by the StreamTokenizer object.
     * @throws DotIOException If the .dot syntax is invalid or incorrect.
     */
    private static void readGraphObject(StreamTokenizer tk, TaskGraph graph) throws IOException, DotIOException {

        String srcNode = null;
        String destNode = null;
        int weight = 0;

        //Check for first word: the name of the node, or the name of the source node of the edge
        if (tk.ttype == StreamTokenizer.TT_WORD) {
            srcNode = tk.sval;
            tk.nextToken();
        } else {
            throw new DotIOException("First token in the line wasn't a node name"); //Error: First token in the line wasn't a node name
        }

        //Check if the element is an edge by checking for the '->' sequence. If it is, then parse the dest node.
        //Note: also checks for different type of hyphen character: as we're not sure if input is '−' or '-'
        if ((tk.ttype == '-') || ((tk.ttype == StreamTokenizer.TT_WORD) && tk.sval.equals("−"))) {
            tk.nextToken();
            if (tk.ttype == '>') {
                tk.nextToken();
            } else {
                throw new DotIOException("Found other character when expecting '>'");
            }
            if (tk.ttype == StreamTokenizer.TT_WORD) {
                destNode = tk.sval;
                tk.nextToken();
            } else {
                throw new DotIOException("destination of edge is not a word"); //Error: destination of edge is not a word"
            }
        }

        //Check for the '[' symbol before the weight property.
        if (tk.ttype == '[') {
            tk.nextToken();
        } else {
            throw new DotIOException("Found other character when expecting '['"); //Error: found other character when expecting '['
        }

        //Check that the weight of the node is correctly notated.
        if ((tk.ttype == StreamTokenizer.TT_WORD) && tk.sval.equalsIgnoreCase("Weight")) {
            tk.nextToken();
        } else {
            throw new DotIOException("Weight of node/edge not specified"); //Error: Weight of node/edge not specified.
        }

        if (tk.ttype == '=') {
            tk.nextToken();
        } else {
            throw new DotIOException("Found other character when expecting '='");
        }

        if (tk.ttype == StreamTokenizer.TT_NUMBER) {
            weight = (int) tk.nval;
            tk.nextToken();
        } else {
            throw new DotIOException("Weight value for node is not a number.");
        }

        //Check that there is a ']' character after weight property.
        if (tk.ttype == ']') {
            tk.nextToken();
        } else {
            throw new DotIOException("Couldn't find ']' character"); //Error: Couldn't find ']' character
        }

        //Determine whether to add task or dependency by checking if destNode has been changed or not.
        if (destNode == null) {
            graph.insertTask(new Task(srcNode, weight));
        } else {
            graph.insertDependency(new Dependency(srcNode, destNode, weight));
        }
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
    public static void write(String outputFile, TaskGraph taskGraph, HashMap<String, Integer> startTimeMap, HashMap<String, Integer> processorMap) throws DotIOException {

        // Use the task graph to get the ordering of the nodes and edges

        // for each node, check if it has a time in the map and which processor it has been assigned to.

        StringBuilder sb = new StringBuilder();

        try {
            PrintWriter writer = new PrintWriter(outputFile);

            // write the first line
            sb.append("digraph ").append(taskGraph.getName()).append(" {\n");

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
