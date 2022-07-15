import java.util.ArrayList;

public class OpNode {
    private int portNum = -1;
    private int dockNum = -1;
    private String nodeName;
    private OpNode parent = null;
    private String edgeArgument = null;
    private String receiveArgument = null;
    private boolean undef = false;

    private ArrayList<String> args = new ArrayList<>();

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


        if(nodeInfo[1].equals("DOCK")){
            dockNum = Integer.parseInt(nodeInfo[2]);

            if(nodeInfo.length > 4){
                for (int i = 3 ; i < nodeInfo.length ; i++){
                    args.add(nodeInfo[i]);
                }

            }else if(nodeInfo.length == 4){
                edgeArgument = nodeInfo[3];
            }
            else {
                System.err.println("Format of the DOCK node: " + nodeInfo[0] + " is invalid\n" +
                        "Example of correct format: <Node name> DOCK <dock number> arg0 arg1 arg2.....");
                System.exit(1);
            }
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

    public ArrayList<String> getArgs() {
        return args;
    }

}

