import java.util.*;


public class DAGGenerator {
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private ArrayList<TreeNode> treeNodes;
    private HashMap<String, Operation> operationHashMap;
    private ArrayList<Edge> DAGEdges = new ArrayList<>();
    private int nodeNumber;

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

    //TODO FIX PROBLEM
    private void generateEdges(String parentLabel, ArrayList<OpNode> childPorts){
        //Operation parentOp = operationHashMap.get(parentLabel);
        Operation parentOp = copyOperationIfUsed(operationHashMap.get(parentLabel));
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

    public ArrayList<Edge> generateDAGEdges(ArrayList<TreeNode> treeNodes){
        this.treeNodes = treeNodes;
        DAGEdges.clear();
        Operation operation;
        Operation lChild;
        Operation rChild;
        nodeNumber = 0;

        Graph dag = new Graph();

        String parentLabel = null;
        ArrayList<OpNode> childPorts = new ArrayList<>();
        Operation childOp;
        TreeNode parentNode = null;

        for (TreeNode node : treeNodes) {

            if(operationHashMap.containsKey(node.getLabel())){

                operation = operationHashMap.get(node.getLabel());
                if(node.getParent() != null){
                    parentNode = node.getParent();
                }


                extractOperationInformation(node, parentNode, dag);



                //operation = copyOperationIfUsed(operationHashMap.get(node.getLabel()));



                // HÄR KOMMENTERA UT
                /*
                if(node.getParent() != null){

                    parentLabel = node.getParent().getLabel();
                    if(operation.getOpType().equals("U")){
                        operation = operationHashMap.get(node.getLabel());

                        lChild = operationHashMap.get(node.getChildren().get(0).getLabel());
                        adjustPortNums(lChild, operation, LEFT);
                        rChild = operationHashMap.get(node.getChildren().get(1).getLabel());
                        adjustPortNums(rChild, operation, RIGHT);

                        childPorts.addAll(lChild.getPortNodeArray());
                        childPorts.addAll(rChild.getPortNodeArray());

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
                    generateEdges(parentLabel, childPorts);



                }else {
                    System.out.println("OPERATION: " + operation.getOpName() + " WITH PORTS: ");
                    for (OpNode pNode: operation.getPortNodeArray()) {
                        System.out.println(pNode.getNodeName());
                    }
                    DAGEdges.addAll(operation.getEdges());
                }



                 */
                //HÄR KOMMENTERA UT



            }
            childPorts.clear();
        }
        //printEdges();
        printDAGEdges(dag.getDAGEdges());
        resetUsageOfOperations();

        return dag.getDAGEdges();
    }


    private void extractOperationInformation(TreeNode treeNode, TreeNode parentNode, Graph dag){
        Operation operation = operationHashMap.get(treeNode.getLabel());
        OpNode fromNode, toNode;
        HashMap<Integer, OpNode> extractedOpNodes = new HashMap<>();
        HashMap<Integer, OpNode> extractedPorts = new HashMap<>();

        for (OpNode node: operation.getPortNodeArray()) {

            //Om noden har en förbestämd edge, skapa den edgen.
            if(operation.getEdgeInfos().containsKey(node)){
                if(extractedOpNodes.containsKey(node.getNodeNum())){
                    fromNode = extractedOpNodes.get(node.getNodeNum());
                }else{
                    //SKAPA NODEN OCH KOLLA VILKEN TYP AV NOD DET ÄR.
                    fromNode = copyNodeToDAG(operation, parentNode, treeNode, node, dag, extractedPorts, extractedOpNodes);
                }
                //HANTERA FÖRBESTÄMDA EDGES
                for(EdgeInfo info : operation.getEdgeInfos().get(node)){
                    if(extractedOpNodes.containsKey(info.getToNode().getNodeNum())){
                        toNode = extractedOpNodes.get(info.getToNode().getNodeNum());
                    }else{
                        toNode = copyNodeToDAG(operation, parentNode, treeNode, info.getToNode(), dag, extractedPorts, extractedOpNodes);
                    }
                    dag.addDAGEdge(new Edge(fromNode, toNode, info.getArg()));
                }

            }else{
                copyNodeToDAG(operation, parentNode, treeNode, node, dag, extractedPorts, extractedOpNodes);
            }
        }
        dag.addAllPorts(extractedPorts);
    }

    private void createDAGEdges(Operation operation, Graph dag, OpNode fromNode, OpNode node, TreeNode currentTree){
        OpNode connectNode;

        if(!currentTree.getChildren().isEmpty() && operationHashMap.get(currentTree.getChildren().get(0).getLabel()).getOpType().equals("U")){
            for (Dock dock : node.getDocks().values()){
                connectNode = dag.getUnionPort(dock.getDockNum());
                if(connectNode != null){
                    connectDocksAndPorts(operation, connectNode, dag, dock, fromNode);
                }
            }
        }else{
            for(Dock dock : node.getDocks().values()){
                connectNode = dag.popPortStack(dock.getDockNum());
                if(connectNode != null){
                    connectDocksAndPorts(operation, connectNode, dag, dock, fromNode);
                }
            }
        }
    }

    private OpNode copyNodeToDAG(Operation operation, TreeNode parentNode, TreeNode currentTreeNode, OpNode node, Graph dag, HashMap<Integer, OpNode> extractedPorts, HashMap<Integer, OpNode> extractedOpNodes){
        OpNode multiNode, portNode, boringNode, dockNode;
        Operation parentOp = null;

        if(parentNode != null && operationHashMap.containsKey(parentNode.getLabel())){
            parentOp = operationHashMap.get(parentNode.getLabel());
        }

        if(!extractedOpNodes.containsKey(node.getNodeNum())){
            if(node.hasPort() && node.isDockNode()){
                multiNode = new OpNode(node.getNodeName(), nodeNumber);

                if(parentOp != null && parentOp.getOpType().equals("U")){
                    adjustPortsForUnion(parentNode, operation, multiNode, node, dag);
                }else{
                    multiNode.setPortNum(node.getPortNum());
                    extractedPorts.put(multiNode.getPortNum(), multiNode);
                }
                createDAGEdges(operation, dag, multiNode, node, currentTreeNode);
                dag.addDAGNode(multiNode);
                extractedOpNodes.put(node.getNodeNum(), multiNode);
                nodeNumber++;
                return multiNode;

            }else if(node.hasPort()){
                if(node.getNodeName().equals("undef" ) && dag.getUndefHashMap().containsKey(node.getNodeNum())){
                    portNode = dag.getUndefHashMap().get(node.getNodeNum());
                }else{
                    portNode = new OpNode(node.getNodeName(), nodeNumber);
                }

                if(parentOp != null && parentOp.getOpType().equals("U")){
                    adjustPortsForUnion(parentNode, operation, portNode, node, dag);
                }
                else {
                    portNode.setPortNum(node.getPortNum());
                    extractedPorts.put(portNode.getPortNum(), portNode);
                }

                extractedOpNodes.put(node.getNodeNum(), portNode);
                dag.addDAGNode(portNode);
                nodeNumber++;
                return portNode;

            }else if(node.isDockNode()){
                dockNode = new OpNode(node.getNodeName(), nodeNumber);
                extractedOpNodes.put(node.getNodeNum(), dockNode);

                createDAGEdges(operation, dag, dockNode, node, currentTreeNode);
                dag.addDAGNode(dockNode);
                nodeNumber++;
                return dockNode;
            }else{
                boringNode = new OpNode(node.getNodeName(), nodeNumber);
                extractedOpNodes.put(node.getNodeNum(), boringNode);
                dag.addDAGNode(boringNode);
                nodeNumber++;
                return boringNode;
            }
        }else{
            return extractedOpNodes.get(node.getNodeNum());
        }
    }

    private void printDAGEdges(ArrayList<Edge> edges){
        for (Edge edge : edges){
            System.out.println(edge.getFromNode().getNodeName() + "(" + edge.getFromNode().getNodeNum()+ ")" + " -> "
                    + edge.getToNode().getNodeName()+ "(" + edge.getToNode().getNodeNum()+ ")" + " with arg: " + edge.getLabel());
        }
    }


    private void connectDocksAndPorts(Operation operation, OpNode connectNode, Graph dag, Dock dock, OpNode fromNode){
        OpNode newPortNode;
        for(OpNode portNode : operation.getPortNodeArray()){
            if(portNode.isUndef() && portNode.getPortNum() == connectNode.getPortNum()){
                newPortNode = new OpNode(connectNode.getNodeName(), connectNode.getNodeNum());
                newPortNode.setPortNum(portNode.getPortNum());
                dag.addUndefNode(portNode.getNodeNum(), connectNode);
                break;
            }
        }

        for(String arg : dock.getArgs()){
            dag.addDAGEdge(new Edge(fromNode, connectNode, arg));
        }
    }

    private void adjustPortsForUnion(TreeNode parentNode, Operation operation, OpNode portNode, OpNode node, Graph dag){
        Operation lChild, rChild;
        lChild = operationHashMap.get(parentNode.getChildren().get(0).getLabel());
        rChild = operationHashMap.get(parentNode.getChildren().get(1).getLabel());
        if(operation.equals(lChild)){
            if(operationHashMap.get(parentNode.getLabel()).getUnionInfos().get(0).getNumberOfPorts() == operation.getPortNodeArray().size()){
                portNode.setPortNum(node.getPortNum());
                dag.addUnionPort(portNode);
            }

        }else if(operation.equals(rChild)){
            if(operationHashMap.get(parentNode.getLabel()).getUnionInfos().get(1).getNumberOfPorts() == operation.getPortNodeArray().size()){
                portNode.setPortNum(node.getPortNum() + operationHashMap.get(parentNode.getLabel()).getUnionInfos().get(0).getNumberOfPorts());
                dag.addUnionPort(portNode);
            }
        }
    }
}
