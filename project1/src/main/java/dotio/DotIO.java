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
    public static TaskGraph read(Reader reader){

        StreamTokenizer tk = new StreamTokenizer(reader);
        //setDotSyntax(tk);


        TaskGraph graph = new TaskGraph(tk.sval);
        try {
            tk.nextToken();
            //Check that input graph is a digraph.
            if ((tk.ttype == StreamTokenizer.TT_WORD) && tk.sval.equalsIgnoreCase("digraph")) {
                tk.nextToken();

                //Read name of graph, can either be in quotes or without quotes
                if ((tk.ttype == '"') || (tk.ttype == StreamTokenizer.TT_WORD)) {
//                    graph.setName(tk.sval);
                    tk.nextToken();
                } else {
                    //Error when we find token other than string or quoted string here
                }

                //Read the "{" character, and start going through each node/edge until "}" character
                if (tk.ttype == '{') {
                    tk.nextToken();

                    //Read each node/edge and add them to TaskGraph object.
                    while (tk.ttype != '}') {
                        tk.nextToken();
                    }

                } else {
                    //Error when we find character other than { here
                }

            } else {
                //Error when first word of dot file is not "digraph"
            }
        } catch (IOException e) {
            //TODO: not sure what to do here, or what cases would cause this exception.
        }

        return graph;
    }

    /**
     * Sets the syntax of a StreamTokenizer object so that it is configured to read a .dot file.
     * @param tk The StreamTokenizer object being configured
     *
    private static void setDotSyntax(StreamTokenizer tk) {
        tk.resetSyntax();
        tk.eolIsSignificant(false);
        tk.slashStarComments(true);
        tk.slashSlashComments(true);
        tk.whitespaceChars(0, ' ');
        tk.wordChars(' ' + 1, '\u00ff');
        tk.ordinaryChar('[');
        tk.ordinaryChar(']');
        tk.ordinaryChar('{');
        tk.ordinaryChar('}');
        tk.ordinaryChar('-');
        tk.ordinaryChar('>');
        tk.ordinaryChar('/');
        tk.ordinaryChar('*');
        tk.quoteChar('"');
        tk.whitespaceChars(';', ';');
        tk.ordinaryChar('=');
    }*/

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
    public static void write(String outputFile, TaskGraph taskGraph, HashMap<String, Integer> startTimeMap, HashMap<String, Integer> processorMap) {

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

                    int communicationTime = task.getCommunicationTime();
                    int startTime = startTimeMap.get(taskName);
                    writer.println("\t" + taskName + "\t\t");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
