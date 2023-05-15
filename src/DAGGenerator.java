import java.util.*;


public class DAGGenerator {

    private final HashMap<String, Operation> operationHashMap;
    private final ArrayList<Edge> DAGEdges = new ArrayList<>();
    private int nodeNumber;
    private Graph dag;
    private Operation curOperation;
    private ContextualNodeStorage contNodeStrge;

    public DAGGenerator(HashMap<String, Operation> operationHashMap){
        this.operationHashMap = operationHashMap;

    }
    private void resetUsageOfOperations(){
        for (Operation op: operationHashMap.values()) {
            op.setUsed(false);
        }
    }

    public ArrayList<Edge> generateDAGEdges(ArrayList<TreeNode> treeNodes){
        DAGEdges.clear();
        nodeNumber = 0;
        dag = new Graph();
        contNodeStrge = new ContextualNodeStorage();
        TreeNode parentNode = null;

        for (TreeNode node : treeNodes) {

            if(operationHashMap.containsKey(node.getLabel())){
                System.out.println("Current Operation: " + node.getLabel());

                if(node.getParent() != null){
                    parentNode = node.getParent();
                }

                extractOperationInformation(node, parentNode);
            }
        }

        printDAGEdges(dag.getDAGEdges());
        resetUsageOfOperations();
        contNodeStrge.printContextualNodes();
        return dag.getDAGEdges();
    }


    private void extractOperationInformation(TreeNode treeNode, TreeNode parentNode){
        Operation operation = operationHashMap.get(treeNode.getLabel());
        Operation lChild;
        setCurrentOperation(operationHashMap.get(treeNode.getLabel()));
        OpNode fromNode, toNode;
        OpNode removeNode = null;
        ArrayList<Integer> createdPortNumbers = new ArrayList<>();
        HashMap<Integer, OpNode> extractedOpNodes = new HashMap<>();
        HashMap<Integer, OpNode> extractedPorts = new HashMap<>();
        ArrayList<OpNode> currOpPorts;

        for (OpNode node: operation.getPortNodeArray()) {
            if(node.hasPort() && createdPortNumbers.contains(node.getPortNum())) continue;
            //Om noden har en förbestämd edge, skapa den edgen.
            if(operation.getEdgeInfos().containsKey(node)){
                if(extractedOpNodes.containsKey(node.getNodeNum())){
                    fromNode = extractedOpNodes.get(node.getNodeNum());
                }else{
                    //SKAPA NODEN OCH KOLLA VILKEN TYP AV NOD DET ÄR.
                    fromNode = copyNodeToDAG(operation, parentNode, treeNode, node, extractedPorts, extractedOpNodes);
                }
                //HANTERA FÖRBESTÄMDA EDGES
                //TODO ÄNDRA FÖR KONTEXTUELLA NODER
                for(EdgeInfo info : operation.getEdgeInfos().get(node)){
                    if(extractedOpNodes.containsKey(info.getToNode().getNodeNum())){
                        toNode = extractedOpNodes.get(info.getToNode().getNodeNum());
                    }else{
                        //TODO KONTEXTUELLA NODER HANTERAS HÄR
                        //KOMMER BARA SKICKA NODES SOM HAR EN PORT ELLER NODER MED PORT OCH DOCK


                        if(contNodeStrge.hasContextualNodesForNodeName(info.getToNode().getNodeName())){
                            System.out.println("GETTING CONT NODE with function " + operation.getOpName());
                            System.out.println("WITH PARENT NODE: " + treeNode.getParent().getLabel());
                            toNode = contNodeStrge.getRandomContextualNode(info.getToNode().getNodeName());
                            createdPortNumbers.add(toNode.getPortNum());
                            currOpPorts = getCurrentOperation().getPortNodeArray();
                            if(treeNode.getParent() != null && operationHashMap.get(treeNode.getParent().getLabel()).isUnion()){
                                //System.out.println("ADDING NODE");
                                //TreeNode parentNode, Operation operation, OpNode portNode, OpNode node
                                adjustPortsForUnion(treeNode.getParent(), operation, toNode, toNode);
                                //toNode.setPortNum(node.getPortNum() + operationHashMap.get(parentNode.getLabel()).getUnionInfos().get(0).getNumberOfPorts());
                                dag.addUnionPort(toNode);
                            }
                            extractedPorts.put(toNode.getPortNum(), toNode);
                            /*
                            for (OpNode port: currOpPorts) {
                                if(port.getPortNum() == toNode.getPortNum()){
                                    removeNode = port;
                                }
                            }

                            if(removeNode != null){
                                currOpPorts.remove(removeNode);
                            }
                            */

                            currOpPorts.add(toNode);


                        }else{

                            toNode = copyNodeToDAG(operation, parentNode, treeNode, info.getToNode(), extractedPorts, extractedOpNodes);
                        }

                       // toNode = copyNodeToDAG(operation, parentNode, treeNode, info.getToNode(), extractedPorts, extractedOpNodes);
                    }
                    dag.addDAGEdge(new Edge(fromNode, toNode, info.getArg()));
                }

            }else{
                copyNodeToDAG(operation, parentNode, treeNode, node, extractedPorts, extractedOpNodes);
            }
        }
        dag.addAllPorts(extractedPorts);
    }

    private void setCurrentOperation(Operation operation){
        curOperation = operation;
    }
    private Operation getCurrentOperation(){
        return curOperation;
    }

    private void createDAGEdges(OpNode fromNode, OpNode node, TreeNode currentTree){
        OpNode connectNode;

        if(!currentTree.getChildren().isEmpty() && //operationHashMap.get(currentTree.getChildren().get(0).getLabel()).getOpType().equals("U")
                operationHashMap.get(currentTree.getChildren().get(0).getLabel()).isUnion()){

            for (Dock dock : node.getDocks().values()){
                connectNode = dag.getUnionPort(dock.getDockNum());
                if(connectNode != null){
                    System.out.println("from " + fromNode.getNodeName() +"(" + fromNode.getNodeNum()+ ")"+ " to " +connectNode.getNodeName() +"(" + connectNode.getNodeNum()+ ")");
                    connectDocksAndPorts(connectNode, dock, fromNode);
                }
            }
        }else{
            for(Dock dock : node.getDocks().values()){
                connectNode = dag.popPortStack(dock.getDockNum());

                if(connectNode != null){
                    System.out.println("from " + fromNode.getNodeName()+ " to " +connectNode.getNodeName());
                    connectDocksAndPorts(connectNode, dock, fromNode);
                }
            }
        }
    }

    private OpNode copyNodeToDAG(Operation operation, TreeNode parentNode, TreeNode currentTreeNode, OpNode node,
                                  HashMap<Integer, OpNode> extractedPorts, HashMap<Integer, OpNode> extractedOpNodes){
        OpNode multiNode, portNode, standardNode;
        Operation parentOp = null;

        if(parentNode != null && operationHashMap.containsKey(parentNode.getLabel())){
            parentOp = operationHashMap.get(parentNode.getLabel());
        }

        if(!extractedOpNodes.containsKey(node.getNodeNum())){
            if(node.hasPort() && node.isDockNode()){
                multiNode = new OpNode(node.getNodeName(), nodeNumber);

                if(parentOp != null && //parentOp.getOpType().equals("U")
                parentOp.isUnion()){
                    System.out.println("WIHU");
                    adjustPortsForUnion(parentNode, operation, multiNode, node);
                }else{
                    multiNode.setPortNum(node.getPortNum());
                    extractedPorts.put(multiNode.getPortNum(), multiNode);
                }
                createDAGEdges(multiNode, node, currentTreeNode);
                dag.addDAGNode(multiNode);
                extractedOpNodes.put(node.getNodeNum(), multiNode);
                nodeNumber++;
                return multiNode;

            }else if(node.hasPort()){
                System.out.println("CREATE PORTNODE");
                if(node.getNodeName().equals("undef" ) && dag.getUndefHashMap().containsKey(node.getNodeNum())){
                    portNode = dag.getUndefHashMap().get(node.getNodeNum());
                }else{
                    portNode = new OpNode(node.getNodeName(), nodeNumber);
                    extractedPorts.put(portNode.getPortNum(), portNode);
                    System.out.println("portNode with number: " + portNode.getNodeNum());
                }

                if(parentOp != null && //parentOp.getOpType().equals("U")
                parentOp.isUnion()){
                    adjustPortsForUnion(parentNode, operation, portNode, node);
                }
                else {
                    portNode.setPortNum(node.getPortNum());
                    extractedPorts.put(portNode.getPortNum(), portNode);
                }

                extractedOpNodes.put(node.getNodeNum(), portNode);
                contNodeStrge.insertNodeToHashSet(portNode);
                dag.addDAGNode(portNode);
                nodeNumber++;
                return portNode;

            }else if(node.isDockNode()){
                /*
                dockNode = new OpNode(node.getNodeName(), nodeNumber);
                extractedOpNodes.put(node.getNodeNum(), dockNode);

                createDAGEdges(dockNode, node, currentTreeNode);
                dag.addDAGNode(dockNode);
                nodeNumber++;
                return dockNode;
                */
                //TODO GÅR ALDRIG IN HIT?
                System.out.println("CREATING dockNode");

                return createDockNode(node, extractedOpNodes, currentTreeNode);
            }else{
                //TODO GÅR ALDRIG IN HIT?
                System.out.println("STANDARD NODE");
                standardNode = new OpNode(node.getNodeName(), nodeNumber);
                contNodeStrge.insertNodeToHashSet(standardNode);
                extractedOpNodes.put(node.getNodeNum(), standardNode);
                dag.addDAGNode(standardNode);
                nodeNumber++;
                return standardNode;
            }
        }else{
            return extractedOpNodes.get(node.getNodeNum());
        }
    }

    private OpNode createPortNode(OpNode node, Operation parentOp, TreeNode parentNode, Operation operation,
                                   HashMap<Integer, OpNode> extractedPorts, HashMap<Integer, OpNode> extractedOpNodes){
        OpNode portNode;
        if(node.getNodeName().equals("undef" ) && dag.getUndefHashMap().containsKey(node.getNodeNum())){
            portNode = dag.getUndefHashMap().get(node.getNodeNum());
        }else{
            portNode = new OpNode(node.getNodeName(), nodeNumber);
        }

        if(parentOp != null && //parentOp.getOpType().equals("U")
        parentOp.isUnion()){
            adjustPortsForUnion(parentNode, operation, portNode, node);
        }
        else {
            portNode.setPortNum(node.getPortNum());
            extractedPorts.put(portNode.getPortNum(), portNode);
        }

        extractedOpNodes.put(node.getNodeNum(), portNode);
        dag.addDAGNode(portNode);
        nodeNumber++;

        return portNode;
    }

    private OpNode createDockNode(OpNode node, HashMap<Integer, OpNode> extractedOpNodes, TreeNode currentTreeNode){
        OpNode dockNode = new OpNode(node.getNodeName(), nodeNumber);
        extractedOpNodes.put(node.getNodeNum(), dockNode);

        createDAGEdges(dockNode, node, currentTreeNode);
        dag.addDAGNode(dockNode);
        nodeNumber++;
        return dockNode;
    }


    private void printDAGEdges(ArrayList<Edge> edges){
        for (Edge edge : edges){
            System.out.println(edge.getFromNode().getNodeName() + "(" + edge.getFromNode().getNodeNum()+ ")" + " -> "
                    + edge.getToNode().getNodeName()+ "(" + edge.getToNode().getNodeNum()+ ")" + " with arg: " + edge.getLabel());
        }
    }


    private void connectDocksAndPorts(OpNode connectNode, Dock dock, OpNode fromNode){
        OpNode newPortNode;
        for(OpNode portNode : getCurrentOperation().getPortNodeArray()){
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

    private void adjustPortsForUnion(TreeNode parentNode, Operation operation, OpNode portNode, OpNode node){
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

    private void contextualNodes(){

    }
}
