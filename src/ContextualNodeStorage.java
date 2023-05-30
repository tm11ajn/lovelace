import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Used to store contextual nodes. Is probably not very efficient.
 *
 * @author Eric Andersson
 */


public class ContextualNodeStorage {
    private HashMap<String, ArrayList<OpNode>> contextNodesHashmap;

    public ContextualNodeStorage(){
        contextNodesHashmap = new HashMap<>();
    }

    //port noder borde inte finnas här??? eller kanske, oklart
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


    //This does not do what it should do
    //TODO FIX but check impact before fix
    public boolean hasContextualNodesForNodeName(String nodeName){

        return contextNodesHashmap.containsKey(nodeName) && contextNodesHashmap.get(nodeName).isEmpty();
    }

    //this does what it should do
    public boolean hasContextualNodeForNodeName(String nodeName){
        return contextNodesHashmap.containsKey(nodeName) && !contextNodesHashmap.get(nodeName).isEmpty();
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
