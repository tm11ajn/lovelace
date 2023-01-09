import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class ContextualNodeStorage {
    private HashMap<String, ArrayList<OpNode>> contextNodesHashmap;

    public ContextualNodeStorage(){
        contextNodesHashmap = new HashMap<>();
    }

    public void insertNodeToHashSet(OpNode node){
        if(contextNodesHashmap.containsKey(node.getNodeName()) && !contextNodesHashmap.get(node.getNodeName()).contains(node)){
            contextNodesHashmap.get(node.getNodeName()).add(node);

        }else{
            System.out.println("HELLO");
            contextNodesHashmap.put(node.getNodeName(), new ArrayList<>());
        }
    }

    public void clearContextNodeStorage(){
        contextNodesHashmap.clear();
    }

    public boolean contextualNodesIsEmpty(String nodeName){
        return contextNodesHashmap.get(nodeName).isEmpty();
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
