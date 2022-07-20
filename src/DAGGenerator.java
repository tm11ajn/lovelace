import java.util.*;
import java.util.Map.Entry;
public class DAGGenerator {

    private ArrayList<TreeNode> treeNodes;
    private HashMap<String, Operation> operationHashMap;
    private ArrayList<Edge> DAGEdges = new ArrayList<>();
    private LinkedList<OpNode> portNodesFromPrevOperationQueue = new LinkedList<>();

    public DAGGenerator(HashMap<String, Operation> operationHashMap){
        this.operationHashMap = operationHashMap;

    }

    public void getPortFromChild(ArrayList<TreeNode> treeNodes){
        this.treeNodes = treeNodes;
        HashMap<String, ArrayList<OpNode>> portHashMap = new HashMap<>();

        for (TreeNode operationNode: treeNodes) {
            if(operationHashMap.containsKey(operationNode.getLabel())){
                Operation operation = operationHashMap.get(operationNode.getLabel());

                if(operationNode.getParent() != null){
                    //System.out.print("Parent operation: " + operationNode.getParent().getLabel()+  " available ports:");
                    for (OpNode portNode: operation.getPortNodeArray()) {

                        if(portHashMap.containsKey(operationNode.getParent().getLabel())){
                            //System.out.print(" " + portNode.getNodeName());
                            portHashMap.get(operationNode.getParent().getLabel()).add(portNode);
                        }else {
                            ArrayList<OpNode> portNodes = new ArrayList<>();
                            //System.out.print(" " + portNode.getNodeName());
                            portNodes.add(portNode);
                            portHashMap.put(operationNode.getParent().getLabel(), portNodes);
                        }

                    }
                    //System.out.print("\n");
                }
            }
        }

        MatchDockWithPort(portHashMap);
        DAGEdges.clear();
    }

    private void MatchDockWithPort(HashMap<String, ArrayList<OpNode>> portHashMap){
        OpNode dockNode;
        OpNode fromNode;
        OpNode toNode;
        Operation currentOperation;
        String operationName;
        String singleArg;
        ArrayList<OpNode> portNodes;
        ArrayList<OpNode> parentDockNodes;

        for (TreeNode node : treeNodes) {
            if(operationHashMap.containsKey(node.getLabel()) && portHashMap.containsKey(node.getLabel())){

                operationName = node.getLabel();
                currentOperation = operationHashMap.get(operationName);
                DAGEdges.addAll(currentOperation.getEdges());

                portNodes = portHashMap.get(operationName);
                parentDockNodes = currentOperation.getDockNodes1();

                for (OpNode portNode: portNodes) {
                    for(OpNode parentDockNode: parentDockNodes){
                        if(parentDockNode.getDocks().containsKey(portNode.getPortNum())){
                            fromNode = parentDockNode;
                            toNode = portNode;
                            if(fromNode.getDocks().get(portNode.getPortNum()).getArgs() != null){
                                for (String arg: fromNode.getDocks().get(portNode.getPortNum()).getArgs()) {
                                    DAGEdges.add(new Edge(fromNode, portNode, arg));
                                    //System.out.println(fromNode.getNodeName() + "->" + toNode.getNodeName() + " with arg " + arg);
                                }
                            }else if(fromNode.getDocks().get(portNode.getPortNum()).getSingleArg() != null){
                                singleArg = fromNode.getDocks().get(portNode.getPortNum()).getSingleArg();

                                DAGEdges.add(new Edge(fromNode, toNode, singleArg));
                                //System.out.println(fromNode.getNodeName() + "->" + toNode.getNodeName() + " with arg " + singleArg);
                            }

                        }
                        setUndefinedNode(node, portNode, portHashMap);
                    }

                    /*
                    if(parentDockNodes.containsKey(portNode.getPortNum())){

                        dockNode = parentDockNodes.get(portNode.getPortNum());
                        createNewEdges(portNode, dockNode);
                        setUndefinedNode(node, portNode, portHashMap);
                    }

                     */
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

    /*
    private void createNewEdges(OpNode portNode, OpNode dockNode){

        if(dockNode.getEdgeArgument() == null){
            for (String arg: dockNode.getArgs()) {
                DAGEdges.add(new Edge(dockNode.getNodeName(), portNode.getNodeName(), arg));
            }
        }else{
            DAGEdges.add(new Edge(dockNode.getNodeName(), portNode.getNodeName(), dockNode.getEdgeArgument()));
        }
    }

     */

    /*
    private void matchDocksWithPorts(ArrayList<OpNode> portNodes, Operation currentOperation ){
        HashMap<Integer, OpNode> parentDockNodes = currentOperation.getDockNodes();
        OpNode dockNode;
        for (OpNode portNode: portNodes) {
            if(parentDockNodes.containsKey(portNode.getPortNum())){



                dockNode = parentDockNodes.get(portNode.getPortNum());
                createNewEdges(portNode, dockNode);
                setUndefinedNode(node, portNode, portHashMap);
            }
        }
    }

     */


    public ArrayList<Edge> buildDAG(){
        System.out.println("---DAG---");
        Operation prevOperation = null;
        String targetNode;
        Operation currentOperation;


        for (TreeNode node : treeNodes) {

            if(operationHashMap.containsKey(node.getLabel())){
                currentOperation = operationHashMap.get(node.getLabel());
                //FUNKAR ENBART OM grammatiken är sorterad från första till sista PORT för varj operation. EXEMPEL
                // A PORT 1
                // B PORT 2
                //skoja funkar ej.


                while(!portNodesFromPrevOperationQueue.isEmpty()){
                    OpNode removedNode = portNodesFromPrevOperationQueue.pollLast();
                    for (OpNode dockNode: currentOperation.getNodes()) {

                        if(dockNode.getDockNum() == removedNode.getPortNum()){
                            int argNum = dockNode.getDockNum()-1;
                            String label = "arg" + argNum;

                            if(removedNode.getNodeName().equals("undef")){
                                targetNode = prevOperation.getPortNode(dockNode.getDockNum()).getNodeName();
                                System.out.println("TARGET NODE: " + targetNode);
                            }else{
                                targetNode = removedNode.getNodeName();
                            }


                            System.out.println("THIS IS THE NEW NODE FROM DOCK " + dockNode.getNodeName() + " TO PORT "
                                    + targetNode + " with label: " + label);
                            Edge newEdge = new Edge(dockNode, removedNode, label);
                        }

                    }
                }

                for (OpNode opNode: currentOperation.getNodes()){
                    if(opNode.hasPort()){
                        portNodesFromPrevOperationQueue.add(opNode);
                    }
                }

                DAGEdges.addAll(currentOperation.getEdges());
                prevOperation = currentOperation;

            }else{
                System.out.println("The given grammar does not support the operation: " + node.getLabel());
                System.exit(1);
            }



        }

        return null;
    }

}
