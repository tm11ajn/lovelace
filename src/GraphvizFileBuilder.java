import java.io.*;
import java.util.*;


public class GraphvizFileBuilder {
    private final File theDir;
    private File treeDir;
    private int DAGindex;
    private ArrayList<Edge> edges;

    public GraphvizFileBuilder(){
        String dirPath = System.getProperty("user.dir") + "/DAGS";
        this.theDir = new File(dirPath);
    }

    public void createDAGDirectory(){

        if(theDir.exists()){

            deleteDAGDir(theDir);
        }

        boolean success = theDir.mkdir();
        if(!success){
            System.err.println("UNABLE TO CREATE DIRECTORY");
            System.exit(1);
        }
    }

    public void createDAGFile(ArrayList<Edge> edges, String currentTree, HashMap<String, String[]> definitions, int DAGNum) throws IOException {
        File resultFile, currentFile;
        this.edges = edges;
        DAGindex = 0;
        boolean success;
        List<OpNode> nodeList;
        ArrayList<OpNode> definedNodes = new ArrayList<>();



        if(definitions.isEmpty()){
            File file = new File(theDir, currentTree + ".txt");

            try(FileOutputStream fileStream = new FileOutputStream(file);
                DataOutputStream data0 = new DataOutputStream(fileStream)) {
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            FileWriter writer = new FileWriter(file);
            initGraphFile(writer);
            writeEdgesToFile(writer, edges);
            writeNodesToFile(writer, edges);
            writer.write("}");
            writer.close();
        }
        else{
            nodeList = fillNodeArray();
            String dirPath = System.getProperty("user.dir") + "/DAGS/" + "RESULT DAG " + DAGNum;
            treeDir = new File(dirPath);
            success = treeDir.mkdir();
            if(!success){
                System.err.println("UNABLE TO CREATE DIRECTORY 3213");
                System.exit(1);
            }
            //rec(nodeList, definitions, definedNodes);


        }
    }

    private void deleteDAGDir(File file){
        File[] files = file.listFiles();
        if(files != null){
            for (File delFile: files) {
                deleteDAGDir(delFile);
            }
        }
        file.delete();
    }

    private void initGraphFile(Writer writer) throws IOException {
        writer.write("digraph G  {\n");
    }

    private void writeEdgesToFile(Writer writer, ArrayList<Edge> edges) throws IOException {
        for (Edge edge: edges) {
            writer.write("\t" + edge.getFromNode().getNodeNum() + " -> " + edge.getToNode().getNodeNum());
            writer.write(" [label=\"" + edge.getLabel() +"\"]\n");
        }
        writer.write("\n");

    }

    private void writeNodesToFile(Writer writer, ArrayList<Edge> edges) throws IOException {
        Set<Integer> usedNodeNums = new HashSet<>();

        for (Edge edge : edges){
            if(!usedNodeNums.contains(edge.getFromNode().getNodeNum())){
                writer.write("\t" + edge.getFromNode().getNodeNum() + " [label=\"" + edge.getFromNode().getNodeName() +"\"" + "]\n");
                usedNodeNums.add(edge.getFromNode().getNodeNum());
            }

            if(!usedNodeNums.contains(edge.getToNode().getNodeNum())){
                writer.write("\t" + edge.getToNode().getNodeNum() + " [label=\"" + edge.getToNode().getNodeName() +"\"" + "]\n");
                usedNodeNums.add(edge.getToNode().getNodeNum());
            }
        }
    }

    private List<OpNode> fillNodeArray(){
        Set<Integer> usedNodeNums = new HashSet<>();
        List<OpNode> nodeArray = new ArrayList<>();

        for (Edge edge : edges){
            if(!usedNodeNums.contains(edge.getFromNode().getNodeNum())){
                nodeArray.add(edge.getFromNode());
                usedNodeNums.add(edge.getFromNode().getNodeNum());
            }

            if(!usedNodeNums.contains(edge.getToNode().getNodeNum())){
                nodeArray.add(edge.getToNode());
                usedNodeNums.add(edge.getToNode().getNodeNum());
            }
        }

        return nodeArray;
    }


    private void rec(List<OpNode> nodeArray, ArrayList<definitionPair> defPairs, ArrayList<OpNode> defNodes) throws IOException {
        boolean foundDef = false;
        OpNode lastNode;
        File file;

        if(nodeArray.size() == 1){
            System.out.println("end of recursion");
            for(definitionPair pair : defPairs){
                if(nodeArray.get(0).equals(pair.getVariable())){
                    for (String definition: pair.getDefinitions()) {
                        lastNode = new OpNode(definition, nodeArray.get(0).getNodeNum());
                        defNodes.add(lastNode);

                        file = new File(treeDir, "DAG " +DAGindex + ".txt");
                        DAGindex++;

                        try(FileOutputStream fileStream = new FileOutputStream(file);
                            DataOutputStream data0 = new DataOutputStream(fileStream)) {
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        FileWriter writer = new FileWriter(file);
                        initGraphFile(writer);
                        writeEdgesToFile(writer, edges);
                        writeDefinedNodesToFile(writer, defNodes);
                        writer.write("}");
                        defNodes.remove(lastNode);
                        foundDef = true;
                    }
                }
            }
            if(!foundDef){
                defNodes.add(nodeArray.get(0));

                file = new File(treeDir, "DAG " +DAGindex + ".txt");
                DAGindex++;

                try(FileOutputStream fileStream = new FileOutputStream(file);
                    DataOutputStream data0 = new DataOutputStream(fileStream)) {
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                FileWriter writer = new FileWriter(file);
                initGraphFile(writer);
                writeEdgesToFile(writer, edges);
                writeDefinedNodesToFile(writer, defNodes);
                writer.write("}");
                defNodes.remove(nodeArray.get(0));
            }

        }else{
            for (int i = 0 ; i < nodeArray.size() ;i++) {
                for ( definitionPair pair : defPairs) {
                    if(nodeArray.get(i).getNodeName().equals(pair.getVariable())){
                        for (String definition: pair.getDefinitions()) {
                            lastNode = new OpNode(definition, nodeArray.get(i).getNodeNum());
                            defNodes.add(lastNode);
                            rec(nodeArray.subList(i+1, nodeArray.size()), defPairs, defNodes);
                            defNodes.remove(lastNode);
                        }
                        foundDef = true;
                        break;
                    }
                }
                if(!foundDef){
                    defNodes.add(nodeArray.get(i));
                    rec(nodeArray.subList(i+1, nodeArray.size()), defPairs, defNodes);
                }
            }
        }
    }

    private void writeDefinedNodesToFile(Writer writer, ArrayList<OpNode> definedNodes) throws IOException {
        for (OpNode definedNode: definedNodes) {
            writer.write("\t" + definedNode.getNodeNum() + " [label=\"" + definedNode.getNodeName() +"\"" + "]\n");
        }

    }

    private void numberOfDefinedDAGS(ArrayList<OpNode> nodes, HashMap<String, String[]> definitions){
        HashMap<String, ArrayList<OpNode>> combinations;
        String multiIndex = "";
        char[] multiIndexChars;
        for (OpNode node: nodes) {
            if(definitions.containsKey(node.getNodeName())){
                for(int i = 0 ; i < definitions.get(node.getNodeName()).length ; i++){


                    if(multiIndex.length() == i) {
                        multiIndex += i;
                    }else {
                        multiIndexChars = multiIndex.toCharArray();
                        multiIndexChars[i] = (char)i;
                        multiIndex = String.valueOf(multiIndexChars);
                    }
                }
            }
        }

    }
}
