import java.util.*;
import java.util.Map.Entry;
public class DAGGenerator {

    private ArrayList<TreeNode> treeNodes;
    private HashMap<String, Operation> operationHashMap;
    private ArrayList<Edge> DAGEdges = new ArrayList<>();
    private LinkedList<OpNode> portNodesFromPrevOperationQueue = new LinkedList<>();

    public DAGGenerator(ArrayList<TreeNode> treeNodes, HashMap<String, Operation> operationHashMap){
        this.treeNodes = treeNodes;
        this.operationHashMap = operationHashMap;

    }

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
                            Edge newEdge = new Edge(dockNode.getNodeName(), removedNode.getNodeName(), label);
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



    public void getPortFromChild(){
        HashMap<String, ArrayList<OpNode>> portHashMap = new HashMap<>();
        for (TreeNode operationNode: treeNodes) {
            if(operationHashMap.containsKey(operationNode.getLabel())){
                Operation operation = operationHashMap.get(operationNode.getLabel());

                for (OpNode opNode: operation.getPortNodeArray()) {

                    if(operationNode.getParent() != null){
                        if(portHashMap.containsKey(operationNode.getParent().getLabel())){
                            portHashMap.get(operationNode.getParent().getLabel()).add(opNode);
                        }else {
                            ArrayList<OpNode> portNodes = new ArrayList<>();
                            portNodes.add(opNode);
                            portHashMap.put(operationNode.getParent().getLabel(), portNodes);
                        }
                    }
                }
            }
        }

        MatchDockWithPort(portHashMap);

        /*
        for (TreeNode node : treeNodes) {
            if(operationHashMap.containsKey(node.getLabel()) && portHashMap.containsKey(node.getLabel())){

                String operationName = node.getLabel();

                ArrayList<OpNode> portNodes = portHashMap.get(operationName);


                for (OpNode portNode: portNodes) {
                    OpNode dockNode = operationHashMap.get(operationName).getDockNodes().get(portNode.getPortNum());

                    System.out.println("Node: " + dockNode.getNodeName() + " with openPort " + portNode.getNodeName() +
                            " with argument: " + dockNode.getEdgeArgument());
                }
            }
        }

         */
    }

    private void MatchDockWithPort(HashMap<String, ArrayList<OpNode>> portHashMap){
        OpNode dockNode;
        Operation currentOperation;
        String operationName;
        ArrayList<OpNode> portNodes;
        HashMap<Integer, OpNode> parentDockNodes;

        for (TreeNode node : treeNodes) {
            if(operationHashMap.containsKey(node.getLabel()) && portHashMap.containsKey(node.getLabel())){

                operationName = node.getLabel();
                currentOperation = operationHashMap.get(operationName);
                DAGEdges.addAll(currentOperation.getEdges());

                portNodes = portHashMap.get(operationName);
                parentDockNodes = currentOperation.getDockNodes();

                for (OpNode portNode: portNodes) {
                    if(parentDockNodes.containsKey(portNode.getPortNum())){

                        dockNode = parentDockNodes.get(portNode.getPortNum());
                        createNewEdges(portNode, dockNode);
                        setUndefinedNode(node, portNode, portHashMap);
                    }
                }
            }
        }

        for (Edge edge: DAGEdges) {
            System.out.println(edge.getFromNode() + " -> " + edge.getToNode() + " with arg: " + edge.getLabel());

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

    private void createNewEdges(OpNode portNode, OpNode dockNode){

        if(dockNode.getEdgeArgument() == null){
            for (String arg: dockNode.getArgs()) {
                DAGEdges.add(new Edge(dockNode.getNodeName(), portNode.getNodeName(), arg));
            }
        }else{
            DAGEdges.add(new Edge(dockNode.getNodeName(), portNode.getNodeName(), dockNode.getEdgeArgument()));
        }
    }

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
}
