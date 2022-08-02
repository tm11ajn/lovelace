import java.util.ArrayList;

public class Operation {
    private final String opName;
    private ArrayList<OpNode> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private final String opType;
    private Boolean isUsed = false;
    private int[] unionPortNumbers;
    private ArrayList<UnionInfo> unionInfos = new ArrayList<>();

    public Operation(String name, String opType){
        this.opName = name;
        this.opType = opType;
    }

    public void addUnionInfo(UnionInfo uInfo){
        unionInfos.add(uInfo);
    }

    public ArrayList<UnionInfo> getUnionInfos() {
        return unionInfos;
    }

    public void setUnionPortNumbers(int lPorts, int rPorts) {
        unionPortNumbers = new int[2];
        unionPortNumbers[0] = lPorts;
        unionPortNumbers[1] = rPorts;
    }

    public int[] getUnionPortNumbers() {
        return unionPortNumbers;
    }

    public Operation(Operation that){
        this(that.opName, that.getOpType());
        for (OpNode node: that.getNodes()) {
            this.nodes.add(node.clone());
        }
        this.edges = that.getEdges();
        this.isUsed = false;
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

    public ArrayList<OpNode> getDockNodes(){
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

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }
}
