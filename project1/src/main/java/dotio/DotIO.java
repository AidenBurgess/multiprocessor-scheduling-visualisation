package main.java.dotio;

import java.io.StreamTokenizer;
import java.io.Reader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
     * @param reader The reader object that encapsulates the stream being read.
     * @return
     */
    public static TaskGraph read(Reader reader) throws DotIOException {

        StreamTokenizer tk = new StreamTokenizer(reader);
        tk.wordChars('-','-');
        tk.wordChars('=','>');
        tk.whitespaceChars(';',';');

        TaskGraph graph;
        try {
            tk.nextToken();

            //Check that input graph is a digraph.
            if ((tk.ttype == StreamTokenizer.TT_WORD) && tk.sval.equalsIgnoreCase("digraph")) {
                tk.nextToken();
            } else {
                throw new DotIOException("Input is not digraph"); //Error: input is not digraph
            }

            //Read name of graph, can either be in quotes or without quotes
            if ((tk.ttype == '"') || (tk.ttype == StreamTokenizer.TT_WORD)) {
                graph = new TaskGraph(tk.sval);
                    tk.nextToken();
            } else {
                throw new DotIOException("graph name is not specified"); //Error: graph name is not specified.
            }

            //Read the "{" character, and start going through each node/edge until "}" character
            if (tk.ttype == '{') {
                tk.nextToken();
            } else {
                throw new DotIOException("no '{' character was found"); //Error: no '{' character was found
            }

            //Read each node/edge and add them to TaskGraph object.
            while (tk.ttype != '}') {
                if (tk.ttype == StreamTokenizer.TT_EOF) {
                    throw new DotIOException("reached end of file before '}'"); //Error: reached end of file before "}"
                }
                readGraphObject(tk, graph);
            }
            return graph;

        } catch (IOException e) {
            throw new DotIOException("Java IO error occured.");
        }
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

        //Check if the element is an edge by checking for the '->' sequence. If it is, then parse the dest node
        if ((tk.ttype == StreamTokenizer.TT_WORD) && (tk.sval.contains("−>"))) {
            tk.nextToken();
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
        if ((tk.ttype == StreamTokenizer.TT_WORD) && tk.sval.startsWith("Weight=")) {
            weight = Integer.parseInt(tk.sval.substring(7));
            tk.nextToken();
        } else {
            throw new DotIOException("Weight of node/edge not specified"); //Error: Weight of node/edge not specified.
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

        try {
            PrintWriter writer = new PrintWriter(outputFile);

            // write the first line
            writer.println("digraph " + taskGraph.getName() + " {");

            // iterate through the task graph tasks
            ArrayList<Task> tasks = taskGraph.getTasks();

            for (Task task : tasks) {
                String taskName = task.getName();

                // if there is a start time
                if (startTimeMap.containsKey(taskName)) {

                    int taskTime = task.getTaskTime();
                    int startTime = startTimeMap.get(taskName);
                    int processor = processorMap.get(taskName);
                    writer.println("\t" + taskName + "\t\t\t[Weight="+taskTime+", Start="+startTime+", Processor="+processor+"];");
                } else {
                    throw new DotIOException("No valid schedule"); // ERROR: No valid schedule
                }
            }

            ArrayList<Dependency> dependencies = taskGraph.getDependencies();

            // add the rest of the dependencies
            for (Dependency dependency : dependencies) {

                String source = dependency.getSource();
                String dest = dependency.getDest();
                int communicationTime = dependency.getCommunicationTime();

                writer.println("\t" + source + " -> " + dest + "\t\t[Weight=" + communicationTime + "];");
            }

            writer.println("}");

            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
