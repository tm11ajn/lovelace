import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Storage of the curren graph built by graph operations.
 *
 * @author Eric Andersson
 */

public class Graph {
    private HashMap<Integer, OpNode> unionPorts = new HashMap<>();
    private HashMap<Integer, OpNode> availablePorts = new HashMap<>();
    private ArrayList<Edge> DAGEdges = new ArrayList<>();
    private ArrayList<OpNode> dagNodes = new ArrayList<>();
    private HashMap<Integer, OpNode> undefHashMap = new HashMap<>();

    public Graph(){

    }

    public void addUnionPort(OpNode port){
        int i = 0;
        i++;
        System.out.println("" +
                "HSIUHIUSHI: " + i);
        unionPorts.put(port.getPortNum(),port);

    }
    public OpNode getUnionPort(int dockNum){
        System.out.println("NUMBER OF UNION PORTS: " + unionPorts.size());
        if(unionPorts.containsKey(dockNum)){
            return unionPorts.get(dockNum);
        }

        return null;
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
        OpNode tempNode = new OpNode(undefNode.getNodeName(), undefNode.getNodeNum(), undefNode.getPortNum());
        undefHashMap.put(nodeNum, tempNode);
    }

    public HashMap<Integer, OpNode> getUndefHashMap() {
        return undefHashMap;
    }
}
