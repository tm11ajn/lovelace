import java.util.ArrayList;
import java.util.HashMap;

public class OpNode {
    private int portNum = -1;
    private int dockNum = -1;

    private HashMap<Integer, Dock> docks = new HashMap<>();
    private int nodeNum;

    private String nodeName;
    private boolean undef = false;

    private ArrayList<String> args = new ArrayList<>();

    public OpNode(String name, int nodeNum){
        this.nodeName = name;
        this.nodeNum = nodeNum;

        if(nodeName.equals("undef")){
            this.undef = true;
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


    public boolean hasPort(){return portNum != -1;}


    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean isUndef(){
        return undef;
    }


    public void addDock(int dockNum, Dock dock) {
        docks.put(dockNum,dock);
    }

    public void setPortNum(String portNum) {
        this.portNum = Integer.parseInt(portNum);
    }

    public HashMap<Integer, Dock> getDocks() {
        return docks;
    }

    public boolean isDockNode(){
        if(!docks.isEmpty()) return true;

        return false;
    }

    public int getNodeNum() {
        return nodeNum;
    }

    public void setNodeNum(int nodeNum) {
        this.nodeNum = nodeNum;
    }

    public void increasePortNum(){
        this.portNum++;
    }
}

