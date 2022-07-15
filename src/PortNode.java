public class PortNode {
    private int portNum;
    private String nodeName;
    private String receiveArg = null;

    public PortNode(String[] info){
        this.nodeName = info[0];
        this.portNum = Integer.parseInt(info[2]);

        if(info[3] != null) this.receiveArg = info[3];
    }

    public String getPortName() {
        return nodeName;
    }

    public int getPortNum() {
        return portNum;
    }

    public String getReceiveArg() {
        return receiveArg;
    }

}
