public class Edge {
    private OpNode fromNode;
    private OpNode toNode;
    private String label;

    /*
    public Edge(String[] edgeinfo){
        this.fromNode = edgeinfo[0];
        this.toNode = edgeinfo[2];
        this.label = edgeinfo[3];
    }

     */

    public Edge(OpNode fromNode, OpNode toNode, String label){
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.label = label;
    }

    public OpNode getFromNode() {
        return fromNode;
    }

    public String getLabel() {
        return label;
    }

    public OpNode getToNode() {
        return toNode;
    }
}
