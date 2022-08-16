import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Graf {
    //LinkedList<OpNode> availablePorts = new LinkedList<>();
    private HashMap<Integer, OpNode> availablePorts = new HashMap<>();
    private ArrayList<Edge> DAGEdges = new ArrayList<>();
    private ArrayList<OpNode> dagNodes = new ArrayList<>();
    private HashMap<Integer, OpNode> undefHashMap = new HashMap<>();

    public Graf(){

    }

    public void addPort(OpNode port){
        availablePorts.put(port.getPortNum(),port);
    }

    public OpNode popPortStack(int dockNum){
        if(availablePorts.containsKey(dockNum)){
            return availablePorts.remove(dockNum);
        }

        return null;
    }

    public void addAllPorts(HashMap<Integer, OpNode> ports){
        availablePorts.putAll(ports);
    }

    public void addDAGEdge(Edge edge){
        DAGEdges.add(edge);
    }

    public HashMap<Integer, OpNode> getAvailablePorts() {
        return availablePorts;
    }

    public void addDAGNode(OpNode node){
        dagNodes.add(node);
    }

    public ArrayList<Edge> getDAGEdges() {
        return DAGEdges;
    }

    public ArrayList<OpNode> getDagNodes() {
        return dagNodes;
    }

    public void addUndefNode(Integer nodeNum, OpNode undefNode){
        undefHashMap.put(nodeNum, undefNode);
    }

    public HashMap<Integer, OpNode> getUndefHashMap() {
        return undefHashMap;
    }
}
