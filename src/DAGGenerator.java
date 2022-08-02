import java.util.*;


public class DAGGenerator {
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private ArrayList<TreeNode> treeNodes;
    private HashMap<String, Operation> operationHashMap;
    private ArrayList<Edge> DAGEdges = new ArrayList<>();
    private int nodeNumber = 1000;

    public DAGGenerator(HashMap<String, Operation> operationHashMap){
        this.operationHashMap = operationHashMap;

    }
    private void resetUsageOfOperations(){
        for (Operation op: operationHashMap.values()) {
            op.setUsed(false);
        }
    }

    private Operation copyOperationIfUsed(Operation operation){
        if(operation.getUsed()){
            operation = new Operation(operation);
            nodeNumber = operation.setNodeNums(nodeNumber);
        }else {
            operation.setUsed(true);
        }
        return operation;
    }





    private void printEdges(){
        System.out.println("EDGES: ");
        for (Edge edge: DAGEdges) {
            System.out.println(edge.getFromNode().getNodeName() + "(" + edge.getFromNode().getNodeNum()+ ")" + " -> "
                    + edge.getToNode().getNodeName()+ "(" + edge.getToNode().getNodeNum()+ ")" + " with arg: " + edge.getLabel());
        }
    }

    private void handlePortsUnion(Operation currentOperation, ArrayList<OpNode> ports){
        for(int i = 0 ; i < ports.size()-1 ; i++){
            for (int j = i+1; j < ports.size(); j++) {
                if(ports.get(i).getPortNum() == ports.get(j).getPortNum()){
                    ports.get(j).increasePortNum();
                }
            }
        }

        if(currentOperation.getOpName().equals("u'")){
            String temp = String.valueOf(ports.get(1).getPortNum());
            ports.get(1).setPortNum(String.valueOf(ports.get(2).getPortNum()));
            ports.get(2).setPortNum(temp);
        }

    }

    private void generateEdges(String parentLabel, ArrayList<OpNode> childPorts){
        Operation parentOp = operationHashMap.get(parentLabel);
        ArrayList<OpNode> dockNodes = parentOp.getDockNodes();
        ArrayList<OpNode> parentPortNodes = parentOp.getPortNodeArray();
        OpNode fromNode;

        for (OpNode portNode : childPorts){
            for(OpNode parentDockNode : dockNodes){
                fromNode = parentDockNode;
                if(parentDockNode.getDocks().get(portNode.getPortNum()).getArgs() != null){
                    for (String arg: fromNode.getDocks().get(portNode.getPortNum()).getArgs()) {
                        DAGEdges.add(new Edge(fromNode, portNode, arg));
                        setUndefinedNode(portNode, parentPortNodes);
                    }
                }

            }
        }

    }


    private void setUndefinedNode(OpNode portNode, ArrayList<OpNode> parentPorts) {
        for (OpNode parPort : parentPorts) {
            if (parPort.isUndef() && parPort.getPortNum() == portNode.getPortNum()) {
                parPort.setNodeName(portNode.getNodeName());
                parPort.setNodeNum(portNode.getNodeNum());
                break;
            }
        }
    }

    public ArrayList<Edge> temp(ArrayList<TreeNode> treeNodes){
        this.treeNodes = treeNodes;
        DAGEdges.clear();
        Operation operation;
        Operation lChild;
        Operation rChild;

        String parentLabel;
        ArrayList<OpNode> childPorts = new ArrayList<>();

        Operation childOp;

        for (TreeNode node : treeNodes) {
            if(operationHashMap.containsKey(node.getLabel())){
                operation = operationHashMap.get(node.getLabel());

                if(node.getParent() != null){

                    operation = copyOperationIfUsed(operation);
                    parentLabel = node.getParent().getLabel();
                    if(operation.getOpType().equals("U")){

                        lChild = operationHashMap.get(node.getChildren().get(0).getLabel());
                        adjustPortNums(lChild, operation, LEFT);
                        rChild = operationHashMap.get(node.getChildren().get(1).getLabel());
                        adjustPortNums(rChild, operation, RIGHT);


                        for(TreeNode child : node.getChildren()){
                            if(operationHashMap.containsKey(child.getLabel())){
                                childOp = operationHashMap.get(child.getLabel());
                                childPorts.addAll(childOp.getPortNodeArray());
                            }
                        }

                    }else {
                        childPorts = operation.getPortNodeArray();
                    }

                    DAGEdges.addAll(operation.getEdges());
                    if(operation.getOpType().equals("U")) {
                        //handlePortsUnion(operation, childPorts);
                    }
                    generateEdges(parentLabel, childPorts);
                }else {
                    System.out.println("OPERATION: " + operation.getOpName() + " WITH PORTS: ");
                    for (OpNode pNode: operation.getPortNodeArray()) {
                        System.out.println(pNode.getNodeName());
                    }
                    DAGEdges.addAll(operation.getEdges());
                }

            }
            childPorts.clear();
        }
        printEdges();
        resetUsageOfOperations();

        return DAGEdges;
    }

    private void adjustPortNums(Operation operation, Operation unionOp, int leftRight){
        ArrayList<OpNode> ports = operation.getPortNodeArray();
        UnionInfo uInfo = unionOp.getUnionInfos().get(leftRight);

        if(uInfo.isIncreaseAll()){
            for (OpNode port: ports) {
                port.increasePortNum();
            }
        }else{
            ArrayList<String> portNums = uInfo.getPortNumbers();
            for (int i = 0; i < ports.size(); i++) {
                ports.get(i).setPortNum(portNums.get(i));
            }
        }
    }
}
