import java.util.*;

public class DAGGenerator {

    private ArrayList<TreeNode> treeNodes;
    private HashMap<String, Operation> operationHashMap;
    private ArrayList<Edge> DAGEdges = new ArrayList<>();
    private int nodeNumber = 1000;

    public DAGGenerator(HashMap<String, Operation> operationHashMap){
        this.operationHashMap = operationHashMap;

    }

    //TODO OLD IMPLEMENTATION REMOVE
    public void getPortFromChild(ArrayList<TreeNode> treeNodes){
        this.treeNodes = treeNodes;
        for (TreeNode node: treeNodes) {
            System.out.println(node.getLabel());
        }
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
        resetUsageOfOperations();
        DAGEdges.clear();
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

    private void matchPortsWithParent(HashMap<String, ArrayList<OpNode>> portHashMap, TreeNode operationNode, Operation operation){
        operation = copyOperationIfUsed(operation);

        for (OpNode portNode: operation.getPortNodeArray()) {
            if(portHashMap.containsKey(operationNode.getParent().getLabel())){
                portHashMap.get(operationNode.getParent().getLabel()).add(portNode);
            }else {
                ArrayList<OpNode> portNodes = new ArrayList<>();
                portNodes.add(portNode);
                portHashMap.put(operationNode.getParent().getLabel(), portNodes);
            }
        }
    }

    private void matchDockWithPort(HashMap<String, ArrayList<OpNode>> portHashMap){
        Operation currentOperation;
        String operationName;

        for (TreeNode node : treeNodes) {
            if(operationHashMap.containsKey(node.getLabel()) && portHashMap.containsKey(node.getLabel())){

                operationName = node.getLabel();
                currentOperation = operationHashMap.get(operationName);
                DAGEdges.addAll(currentOperation.getEdges());

                ArrayList<OpNode> ports = portHashMap.get(currentOperation.getOpName());
                handlePortsUnion(currentOperation, ports);
                generateEdges2(currentOperation, node, portHashMap);
            }
        }

        printEdges();
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

    private void generateEdges2(Operation currentOperation, TreeNode node, HashMap<String, ArrayList<OpNode>> portHashMap){

        ArrayList<OpNode> portNodes = portHashMap.get(currentOperation.getOpName());
        ArrayList<OpNode> parentDockNodes = currentOperation.getDockNodes();
        ArrayList<OpNode> parentPortNodes = currentOperation.getPortNodeArray();
        OpNode fromNode;

        for (OpNode portNode: portNodes) {
            for(OpNode parentDockNode: parentDockNodes){
                if(parentDockNode.getDocks().containsKey(portNode.getPortNum())){
                    fromNode = parentDockNode;
                    if(fromNode.getDocks().get(portNode.getPortNum()).getArgs() != null){
                        for (String arg: fromNode.getDocks().get(portNode.getPortNum()).getArgs()) {
                            DAGEdges.add(new Edge(fromNode, portNode, arg));
                            setUndefinedNode(portNode, parentPortNodes);
                        }
                    }
                }
            }
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
                break;
            }
        }
    }

    public ArrayList<Edge> temp(ArrayList<TreeNode> treeNodes){
        this.treeNodes = treeNodes;
        DAGEdges.clear();
        Operation operation;

        String parentLabel;
        ArrayList<OpNode> childPorts = new ArrayList<>();
        int[] numOfPortsUnion = new int[2];

        Operation childOp;

        for (TreeNode node : treeNodes) {
            if(operationHashMap.containsKey(node.getLabel())){
                operation = operationHashMap.get(node.getLabel());

                if(node.getParent() != null){

                    operation = copyOperationIfUsed(operation);
                    parentLabel = node.getParent().getLabel();
                    if(operation.getOpType().equals("U")){
                        numOfPortsUnion = operation.getUnionPortNumbers();

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
                        handlePortsUnion(operation, childPorts);
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
}
