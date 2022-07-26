import java.util.ArrayList;

public class Operation {
    private final String opName;
    private ArrayList<OpNode> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private final String opType;

    public Operation(String name, String opType){
        this.opName = name;
        this.opType = opType;
    }

    public Operation(Operation that){
        this(that.opName, that.getOpType());
        this.nodes = that.getNodes();
        this.edges = that.getEdges();
    }

    public void addEdge(Edge edge){
        edges.add(edge);
    }

    public Integer setNodeNums(Integer nodeIndex){
        for (OpNode node: nodes) {
            node.setNodeNum(nodeIndex);
            nodeIndex++;
        }

        return nodeIndex;
    }

    public void addNode(OpNode node){
        nodes.add(node);
    }

    public String getOpName() {
        return opName;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<OpNode> getNodes() {
        return nodes;
    }

    public ArrayList<OpNode> getDockNodes1(){
        ArrayList<OpNode> dockNodes = new ArrayList<>();
        for (OpNode node: nodes) {
            if(node.isDockNode()) dockNodes.add(node);
        }

        return dockNodes;
    }

    public ArrayList<OpNode> getPortNodeArray(){
        ArrayList<OpNode> portNodes = new ArrayList<>();
        for(OpNode node : nodes){
            if(node.hasPort()) portNodes.add(node);
        }

        return portNodes;
    }

    public String getOpType() {
        return opType;
    }
}
