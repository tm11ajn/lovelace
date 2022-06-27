public class Edge {
    private String fromNode;
    private String toNode;
    private String label;

    public Edge(String[] edgeinfo){
        this.fromNode = edgeinfo[0];
        this.toNode = edgeinfo[2];
        this.label = edgeinfo[3];
    }

    public String getFromNode() {
        return fromNode;
    }

    public String getLabel() {
        return label;
    }

    public String getToNode() {
        return toNode;
    }
}
