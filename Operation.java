import java.util.ArrayList;

public class Operation {
    private String opName;
    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();

    public Operation(String name){
        this.opName = name;

    }

    public void addEdge(Edge edge){
        edges.add(edge);
    }

    public void addNode(Node node){
        nodes.add(node);
    }

    public String getOpName() {
        return opName;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public boolean validateEdges(){

        for (Edge edge: edges) {
            checkNodeExistence(edge.getToNode());
            checkNodeExistence(edge.getFromNode());

        }

        return true;
    }

    private boolean checkNodeExistence(String nodeName){
        for(Node node : nodes){
            if(node.getNodeName().equals(nodeName)) return true;
        }

        System.out.println("invalid edge, node " + nodeName + " does not exist");
        return false;
    }
}
