import java.lang.reflect.Array;
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

    //TODO börja med nodeNumber 0 för varje graf
    public ArrayList<Edge> temp(ArrayList<TreeNode> treeNodes){
        this.treeNodes = treeNodes;
        DAGEdges.clear();
        Operation operation;
        Operation lChild;
        Operation rChild;
        nodeNumber = 0;

        Graf dag = new Graf();

        String parentLabel;
        ArrayList<OpNode> childPorts = new ArrayList<>();

        Operation childOp;

        for (TreeNode node : treeNodes) {

            if(operationHashMap.containsKey(node.getLabel())){

                operation = operationHashMap.get(node.getLabel());

                extractOperationInformation(operation, dag);



                //operation = copyOperationIfUsed(operationHashMap.get(node.getLabel()));




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



            }
            childPorts.clear();
        }
        //printEdges();
        printDAGEdges(dag.getDAGEdges());
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

    private void extractOperationInformation(Operation operation, Graf dag){

        OpNode portNode, multiNode, dockNode, boringNode, fromNode, toNode;
        HashMap<Integer, OpNode> extractedOpNodes = new HashMap<>();
        HashMap<Integer, OpNode> extractedPorts = new HashMap<>();


        for (OpNode node: operation.getPortNodeArray()) {

            //Om noden har en förbestämd edge, skapa den edgen.
            if(operation.getEdgeInfos().containsKey(node)){
                if(extractedOpNodes.containsKey(node.getNodeNum())){
                    fromNode = extractedOpNodes.get(node.getNodeNum());
                }else{
                    //SKAPA NODEN OCH KOLLA VILKEN TYP AV NOD DET ÄR.
                    fromNode = copyNodeToDAG(node, dag, extractedPorts, extractedOpNodes);
                }

                for(EdgeInfo info : operation.getEdgeInfos().get(node)){
                    if(extractedOpNodes.containsKey(info.getToNode().getNodeNum())){
                        toNode = extractedOpNodes.get(info.getToNode().getNodeNum());
                    }else{
                        toNode = copyNodeToDAG(info.getToNode(), dag, extractedPorts, extractedOpNodes);
                    }
                    dag.addDAGEdge(new Edge(fromNode, toNode, info.getArg()));
                }

            }else{
                copyNodeToDAG(node, dag, extractedPorts, extractedOpNodes);
            }
        }


        dag.addAllPorts(extractedPorts);
    }

    private void createDAGEdges(Graf dag, OpNode fromNode){
        System.out.println("wihu");
        OpNode connectNode;
        for(Dock dock : fromNode.getDocks().values()){
            System.out.println("in docks");
            connectNode = dag.popPortStack(dock.getDockNum());

            if(connectNode != null){
                for(String arg : dock.getArgs()){
                    dag.addDAGEdge(new Edge(fromNode, connectNode, arg));
                }
            }

        }
    }

    private OpNode copyNodeToDAG(OpNode node, Graf dag, HashMap<Integer, OpNode> extractedPorts, HashMap<Integer, OpNode> extractedOpNodes){
        OpNode multiNode, portNode, boringNode, dockNode;
        if(node.hasPort() && node.isDockNode()){
            multiNode = new OpNode(node.getNodeName(), nodeNumber);
            multiNode.setPortNum(node.getPortNum());
            extractedPorts.put(multiNode.getPortNum(), multiNode);
            createDAGEdges(dag, multiNode);
            dag.addDAGNode(multiNode);
            extractedOpNodes.put(node.getNodeNum(), multiNode);
            nodeNumber++;
            return multiNode;

        }else if(node.hasPort()){
            portNode = new OpNode(node.getNodeName(), nodeNumber);
            portNode.setPortNum(node.getPortNum());
            extractedOpNodes.put(node.getNodeNum(), portNode);
            extractedPorts.put(portNode.getPortNum(), portNode);
            dag.addDAGNode(portNode);
            nodeNumber++;
            return portNode;

        }else if(node.isDockNode()){
            //CONNECT DOCK WITH PORT
            dockNode = new OpNode(node.getNodeName(), nodeNumber);
            extractedOpNodes.put(node.getNodeNum(), dockNode);

            createDAGEdges(dag, dockNode);
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
    }

    private void printDAGEdges(ArrayList<Edge> edges){
        for (Edge edge : edges){
            System.out.println(edge.getFromNode().getNodeName() + "(" + edge.getFromNode().getNodeNum()+ ")" + " -> "
                    + edge.getToNode().getNodeName()+ "(" + edge.getToNode().getNodeNum()+ ")" + " with arg: " + edge.getLabel());
        }
    }
}
