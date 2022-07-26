import java.util.*;

public class DAGGenerator {

    private ArrayList<TreeNode> treeNodes;
    private HashMap<String, Operation> operationHashMap;
    private ArrayList<Edge> DAGEdges = new ArrayList<>();
    private LinkedList<OpNode> portNodesFromPrevOperationQueue = new LinkedList<>();
    private int nodeNumber = 1000;

    public DAGGenerator(HashMap<String, Operation> operationHashMap){
        this.operationHashMap = operationHashMap;

    }

    public void getPortFromChild(ArrayList<TreeNode> treeNodes){
        this.treeNodes = treeNodes;
        Operation operation;
        Operation childOp;
        ArrayList<OpNode> childPorts = new ArrayList<>();
        HashMap<String, ArrayList<OpNode>> portHashMap = new HashMap<>();

        for (TreeNode operationNode: treeNodes) {
            if(operationHashMap.containsKey(operationNode.getLabel())){
                operation = operationHashMap.get(operationNode.getLabel());

                if(operationNode.getParent() != null){
                    if(operation.getOpType().equals("U")){
                        for(TreeNode child : operationNode.getChildren()){
                            if(operationHashMap.containsKey(child.getLabel())){
                                childOp = operationHashMap.get(child.getLabel());
                                if(childOp.getUsed()){
                                    childOp = new Operation(childOp);
                                    nodeNumber = childOp.setNodeNums(nodeNumber);
                                }else {
                                    childOp.setUsed(true);
                                }

                                childPorts.addAll(childOp.getPortNodeArray());
                            }
                        }
                        if(!childPorts.isEmpty()){
                            portHashMap.put(operationNode.getParent().getLabel(), childPorts);
                        }
                    }


                    matchPortsWithParent(portHashMap, operationNode, operation);
                }
            }
        }
        matchDockWithPort(portHashMap);
        DAGEdges.clear();
    }

    private void matchPortsWithParent(HashMap<String, ArrayList<OpNode>> portHashMap, TreeNode operationNode, Operation operation){
        System.out.print("Operation:  " + operationHashMap.get(operationNode.getParent().getLabel()).getOpName()  + " with port nodes:");
        for (OpNode portNode: operation.getPortNodeArray()) {
            System.out.print(" " + portNode.getNodeName() + "(" + portNode.getNodeNum() +")");
            if(portHashMap.containsKey(operationNode.getParent().getLabel())){
                portHashMap.get(operationNode.getParent().getLabel()).add(portNode);
            }else {
                ArrayList<OpNode> portNodes = new ArrayList<>();
                portNodes.add(portNode);
                portHashMap.put(operationNode.getParent().getLabel(), portNodes);
            }
        }
        System.out.print("\n");
    }

    private void matchDockWithPort(HashMap<String, ArrayList<OpNode>> portHashMap){
        OpNode fromNode;
        OpNode toNode;
        Operation currentOperation;
        String operationName;
        ArrayList<OpNode> portNodes;
        ArrayList<OpNode> parentDockNodes;

        for (TreeNode node : treeNodes) {
            if(operationHashMap.containsKey(node.getLabel()) && portHashMap.containsKey(node.getLabel())){

                operationName = node.getLabel();

                currentOperation = operationHashMap.get(operationName);


                DAGEdges.addAll(currentOperation.getEdges());

                portNodes = portHashMap.get(operationName);

                if(currentOperation.getOpType().equals("U")){
                    ArrayList<OpNode> ports = portHashMap.get(operationName);
                    for(int i = 0 ; i < ports.size()-1 ; i++){
                        if(ports.get(i).getPortNum() == ports.get(i+1).getPortNum()){
                            ports.get(i+1).increasePortNum();
                        }
                    }
                }

                parentDockNodes = currentOperation.getDockNodes1();

                for (OpNode portNode: portNodes) {
                    for(OpNode parentDockNode: parentDockNodes){
                        if(parentDockNode.getDocks().containsKey(portNode.getPortNum())){
                            fromNode = parentDockNode;
                            if(fromNode.getDocks().get(portNode.getPortNum()).getArgs() != null){
                                for (String arg: fromNode.getDocks().get(portNode.getPortNum()).getArgs()) {
                                    DAGEdges.add(new Edge(fromNode, portNode, arg));
                                }
                            }
                        }
                        setUndefinedNode(node, portNode, portHashMap);
                    }
                }
            }
        }
        System.out.println("EDGES: ");
        for (Edge edge: DAGEdges) {
            System.out.println(edge.getFromNode().getNodeName() + "(" + edge.getFromNode().getNodeNum()+ ")" + " -> "
                    + edge.getToNode().getNodeName()+ "(" + edge.getToNode().getNodeNum()+ ")" + " with arg: " + edge.getLabel());

        }
    }

    private void setUndefinedNode(TreeNode node, OpNode portNode, HashMap<String, ArrayList<OpNode>> portHashMap) {
        if(node.getParent() != null){
            ArrayList<OpNode> parentPorts;
            parentPorts = portHashMap.get(node.getParent().getLabel());
            for (OpNode parPort : parentPorts) {
                if (parPort.isUndef() && parPort.getPortNum() == portNode.getPortNum()) {
                    parPort.setNodeName(portNode.getNodeName());
                    break;
                }
            }
        }
    }

    private void matchPortAndDockNums(ArrayList<OpNode> portNodes, ArrayList<OpNode> dockNodes){
        ArrayList<Dock> unmatchedDock = new ArrayList<>();

        for (OpNode port: portNodes) {
            for(OpNode dockNode : dockNodes){


            }

        }
    }
}
