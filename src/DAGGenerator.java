import java.util.*;
import java.util.Map.Entry;
public class DAGGenerator {

    private ArrayList<TreeNode> treeNodes;
    private HashMap<String, Operation> operationHashMap;
    private Stack<TreeNode> portStack = new Stack<>();
    private ArrayList<Edge> DAGEdges = new ArrayList<>();
    private ArrayList<OpNode> pockNodes = new ArrayList<>();
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

    private Edge createEdgesBetweenPortAndDock(String fromNode, String toNode){


        return null;
    }

    public void GetPortFromChild(){
        HashMap<String, ArrayList<OpNode>> portHashMap = new HashMap<>();
        for (TreeNode node: treeNodes) {
            if(operationHashMap.containsKey(node.getLabel())){
                Operation operation = operationHashMap.get(node.getLabel());

                for (OpNode opNode: operation.getNodes()) {

                    if(opNode.hasPort() && node.getParent() != null){
                        if(portHashMap.containsKey(node.getParent().getLabel())){
                            portHashMap.get(node.getParent().getLabel()).add(opNode);
                        }else {
                            ArrayList<OpNode> portNodes = new ArrayList<>();
                            portNodes.add(opNode);
                            portHashMap.put(node.getParent().getLabel(), portNodes);
                        }
                        //String key = node.getParent().getLabel() + opNode.getNodeName();
                        //portHashMap.put(node.getParent().getLabel(), opNode);
                    }
                }
            }
        }

        for (TreeNode node : treeNodes) {
            if(operationHashMap.containsKey(node.getLabel()) && portHashMap.containsKey(node.getLabel())){

                ArrayList<OpNode> portNodes = portHashMap.get(node.getLabel());

                for (OpNode portNode: portNodes) {
                    OpNode dockNode = operationHashMap.get(node.getLabel()).getDockNodes().get(portNode.getPortNum());

                    System.out.println("Node: " + node.getLabel() + " with openPort " + portNode.getNodeName() + " with argument: " + dockNode.getEdgeArgument()) ;
                }
            }
        }
    }
}
