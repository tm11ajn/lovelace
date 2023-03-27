public class GraphVizNode {
    private final int nodeNum;
    private final String nodeName;

    public GraphVizNode(int nodeNum, String nodeName){
        this.nodeName = nodeName;
        this.nodeNum = nodeNum;
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getNodeNum() {
        return nodeNum;
    }
}
