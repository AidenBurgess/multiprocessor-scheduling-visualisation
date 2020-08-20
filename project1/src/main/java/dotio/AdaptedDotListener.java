package main.java.dotio;

import main.java.dotio.antlr.DOTBaseListener;
import main.java.dotio.antlr.DOTParser;

public class AdaptedDotListener extends DOTBaseListener {

    private TaskGraph _taskGraph;

    public AdaptedDotListener(TaskGraph graph) {
        super();
        _taskGraph = graph;
    }
    @Override
    public void exitGraph(DOTParser.GraphContext ctx) {
        if (ctx.id() != null) {
            _taskGraph.setName(ctx.id().getText().replaceAll("^\"|\"$", ""));
        }
    }

    @Override
    public void exitNode_stmt(DOTParser.Node_stmtContext ctx) {
        String taskName = ctx.node_id().id().getText();
        int taskWeight = getWeightFromAttrList(ctx.attr_list());
        _taskGraph.insertTask(new Task(taskName, taskWeight));
    }

    @Override
    public void exitEdge_stmt(DOTParser.Edge_stmtContext ctx) {
        if (ctx.edgeRHS().node_id().size() != 1) {
            //Skip this edge, maybe throw exception?
        } else {
            DOTParser.Node_idContext nodeList = ctx.node_id();
            String src = ctx.node_id().id().getText();
            String dest = ctx.edgeRHS().node_id(0).id().getText();
            int weight = getWeightFromAttrList(ctx.attr_list());
            _taskGraph.insertDependency(new Dependency(src, dest, weight));
        }
    }

    private int getWeightFromAttrList(DOTParser.Attr_listContext ctx) {
        for (DOTParser.A_listContext attr : ctx.a_list()) {
            if (attr.id(0).getText().equalsIgnoreCase("WEIGHT")) {
                return Integer.parseInt(attr.id(1).getText());
            }
        }
        return -1;
    }
}
