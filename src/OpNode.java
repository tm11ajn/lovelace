public class OpNode {
    private int portNum = -1;
    private int dockNum = -1;
    private String nodeName;
    private OpNode parent = null;
    private String edgeArgument = null;
    private String receiveArgument = null;
    private boolean undef = false;

    public OpNode(String[] info){
        this.nodeName = info[0];

        if(nodeName.equals("undef")){
            this.undef = true;
        }

        if(info.length > 1){
            setDockAndPort(info);
        }
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

        /*
        for( int i = 0 ; i < nodeInfo.length-1 ; i++){
            if(nodeInfo[i].contains("DOCK")){
                dockNum = Integer.parseInt(nodeInfo[i+1]);
                edgeArgument = nodeInfo[nodeInfo.length-1];

            }

            if(nodeInfo[i].contains("PORT")){
                portNum = Integer.parseInt(nodeInfo[i+1]);
            }
        }

         */

        if(nodeInfo[1].equals("DOCK")){
            dockNum = Integer.parseInt(nodeInfo[2]);
            edgeArgument = nodeInfo[3];
        }

        if(nodeInfo[1].equals("PORT")){
            portNum = Integer.parseInt(nodeInfo[2]);

        //    if(nodeInfo.length > 3) this.receiveArgument = nodeInfo[3];
        }
    }

    public OpNode getParent() {
        return parent;
    }

    public void setParent(OpNode parent) {
        this.parent = parent;
    }

    public boolean hasPort(){return portNum != -1;}

    public boolean hasDock(){return dockNum != -1;}

    public String getEdgeArgument() {
        return edgeArgument;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getReceiveArgument() {
        return receiveArgument;
    }

    public boolean isUndef(){
        return undef;
    }
}

