public class EdgeInfo {
    private final OpNode toNode;
    private final String arg;

    public EdgeInfo(OpNode toNode, String arg){
        this.toNode = toNode;
        this.arg = arg;
    }

    public OpNode getToNode() {
        return toNode;
    }

    public String getArg() {
        return arg;
    }
}
