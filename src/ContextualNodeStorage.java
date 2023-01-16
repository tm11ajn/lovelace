import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class ContextualNodeStorage {
    private HashMap<String, ArrayList<OpNode>> contextNodesHashmap;

    public ContextualNodeStorage(){
        contextNodesHashmap = new HashMap<>();
    }

    public void insertNodeToHashSet(OpNode node){
        ArrayList<OpNode> nodes;
        if(contextNodesHashmap.containsKey(node.getNodeName())){
            System.out.println("INSERTING CONTEXTNODE INTO ARRAY" + node.getNodeName());
            nodes = contextNodesHashmap.get(node.getNodeName());
            if(!nodes.contains(node)){
                nodes.add(node);
            }

        }else{
            System.out.println("CREATING CONTEXT ARRAY FOR NODE: " + node.getNodeName() + " with node number: " + node.getNodeNum());
            ArrayList<OpNode> nodeList= new ArrayList<>();
            nodeList.add(node);
            contextNodesHashmap.put(node.getNodeName(), nodeList);

        }
    }

    public void clearContextNodeStorage(){
        contextNodesHashmap.clear();
    }

    public boolean hasContextualNodesForNodeName(String nodeName){
        return !contextNodesHashmap.get(nodeName).isEmpty();
    }

    public OpNode getRandomContextualNode(String nodeName){
        int size, rand;

        size = contextNodesHashmap.get(nodeName).size();
        rand = new Random().nextInt(size);
        return contextNodesHashmap.get(nodeName).get(rand);
    }

    public void printContextualNodes(){
        for (String key : contextNodesHashmap.keySet()) {
            System.out.println("Contextual nodes with name: " + key);
            for (OpNode node: contextNodesHashmap.get(key)) {
                System.out.println("\t nodeNumber: " + node.getNodeNum());
            }
        }
    }
}
