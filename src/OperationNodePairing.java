public class OperationNodePairing {
    private String opName;
    private String nodeName;

    public OperationNodePairing(String opName, String nodeName){
        this.opName = opName;
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getOpName() {
        return opName;
    }
}
