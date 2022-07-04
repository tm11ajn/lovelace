public class DockNode {
    private int dockNum;
    private String nodeName;
    private String argName;
    private boolean isConnected = false;

    public DockNode(String[] info){
        this.nodeName = info[0];
        this.dockNum = Integer.parseInt(info[2]);
        this.argName = info[3];
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getDockNum() {
        return dockNum;
    }

    public String getArgName() {
        return argName;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
