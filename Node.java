public class Node {
    private int portNum = -1;
    private int dockNum = -1;
    private String nodeName;

    public Node(String[] info){
        this.nodeName = info[0];
        setDockAndPort(info);
    }

    public int getDockNum() {
        return dockNum;
    }

    public int getPortNum() {
        return portNum;
    }

    public String getNodeName() {
        return nodeName;
    }

    private void setDockAndPort(String[] nodeInfo){

        for( int i = 0 ; i < nodeInfo.length-1 ; i++){
            if(nodeInfo[i].contains("DOCK")){
                dockNum = Integer.parseInt(nodeInfo[i+1]);
            }

            if(nodeInfo[i].contains("PORT")){
                portNum = Integer.parseInt(nodeInfo[i+1]);
            }
        }
    }
}

