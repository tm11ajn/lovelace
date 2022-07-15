import java.util.ArrayList;
import java.util.HashMap;

public class Operation {
    private String opName;
    private ArrayList<OpNode> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();

    public Operation(String name){
        this.opName = name;

    }

    public void addEdge(Edge edge){
        edges.add(edge);
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

    public boolean validateEdges(){

        for (Edge edge: edges) {
            checkNodeExistence(edge.getToNode());
            checkNodeExistence(edge.getFromNode());

        }

        return true;
    }

    private boolean checkNodeExistence(String nodeName){
        for(OpNode node : nodes){
            if(node.getNodeName().equals(nodeName)) return true;
        }

        System.out.println("invalid edge, node " + nodeName + " does not exist");
        return false;
    }

    public OpNode getPortNode(int dockNum){
        ArrayList<OpNode> portNodes = new ArrayList<>();
        for (OpNode node: nodes) {
            if(node.getPortNum() == dockNum ) return node;

        }

        return null;
    }

    public HashMap<Integer, OpNode> getDockNodes(){
        HashMap<Integer, OpNode> dockMap = new HashMap<>();
        for (OpNode node: nodes) {
            if(node.getDockNum() != -1) dockMap.put(node.getDockNum(), node);
        }

        return dockMap;
    }

    public ArrayList<OpNode> getDockNodes2(){
        ArrayList<OpNode> dockNodes = new ArrayList<>();
        for (OpNode node: nodes) {
            if(node.getDockNum() != -1) dockNodes.add(node);
        }

        return dockNodes;
    }

    public HashMap<Integer, OpNode> getPortNodes(){
        HashMap<Integer, OpNode> portMap = new HashMap<>();
        for (OpNode node: nodes) {
            if(node.getPortNum() != -1) portMap.put(node.getDockNum(), node);
        }

        return portMap;
    }

    public ArrayList<OpNode> getPortNodeArray(){
        ArrayList<OpNode> portNodes = new ArrayList<>();
        for(OpNode node : nodes){
            if(node.hasPort()) portNodes.add(node);
        }

        return portNodes;
    }

    public HashMap<Integer, OpNode> getUndefPorts(){
        HashMap<Integer, OpNode> UndefPorts = new HashMap<>();

        for (OpNode node: nodes) {
            if(node.isUndef() && node.hasPort()) UndefPorts.put(node.getPortNum(), node);
        }

        return UndefPorts;
    }
}
