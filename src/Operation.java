import java.util.ArrayList;
import java.util.HashMap;

public class Operation {
    private final String opName;
    private contextRule contextRule;
    private ArrayList<OpNode> nodes = new ArrayList<>();
    private ArrayList<OpNode> contNodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private HashMap<OpNode, ArrayList<EdgeInfo>> edgeInfos = new HashMap<>();
    private final String opType;
    private Boolean isUsed = false;
    private int[] unionPortNumbers;
    private ArrayList<UnionInfo> unionInfos = new ArrayList<>();
    ArrayList<OpNode> portNodes;



    public Operation(String name, String opType){
        this.opName = name;
        this.opType = opType;
    }

    public void setContextRule(contextRule contextRule) {
        this.contextRule = contextRule;
    }

    public contextRule getContextRule() {
        return contextRule;
    }

    public void addUnionInfo(UnionInfo uInfo){
        unionInfos.add(uInfo);
    }

    public ArrayList<UnionInfo> getUnionInfos() {
        return unionInfos;
    }

    public void addContNode(OpNode contNode){
        contNodes.add(contNode);
    }


    public void addEdgeInfo(OpNode fromNode, EdgeInfo edgeInfo){
        ArrayList<EdgeInfo> infos;
        if(edgeInfos.containsKey(fromNode)){
            edgeInfos.get(fromNode).add(edgeInfo);
        }else{
            infos = new ArrayList<>();
            infos.add(edgeInfo);
            edgeInfos.put(fromNode, infos);
        }
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
       portNodes = new ArrayList<>();
        for(OpNode node : nodes){
            if(node.hasPort()) portNodes.add(node);
        }

        return portNodes;
    }

    public void addPortNodeToOperation(OpNode port){
        OpNode removePort = null;
        for (OpNode currPort: portNodes) {
            if(currPort.getPortNum() == port.getPortNum()){
                removePort= currPort;
                break;
            }
        }

        if(removePort != null){
            portNodes.remove(removePort);
        }

        portNodes.add(port);
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

    public HashMap<OpNode, ArrayList<EdgeInfo>> getEdgeInfos() {
        return edgeInfos;
    }
}
