import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


public class GraphvizFileBuilder {
    private final File theDir;
    private File treeDir;
    private int DAGindex;
    private ArrayList<Edge> edges;
    private ArrayList<String> variableRep;

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

    public void createDAGFile(ArrayList<Edge> edges, String currentTree, ArrayList<definitionPair> definitions, int DAGNum) throws IOException {
        File resultFile, currentFile;
        this.edges = edges;
        DAGindex = 0;
        boolean success;
        ArrayList<OpNode> nodeList;
        int numberOfCombinations;
        HashMap<String, Integer> numberOfOccurrences;
        ArrayList<ArrayList<String>> combinations;
        ArrayList<String> stringNodes = new ArrayList<>();



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
            numberOfOccurrences = calculateNumberOfOccurrencesForNodeName(nodeList);
            numberOfCombinations = calculateNumberOfCombinations(definitions, numberOfOccurrences);
            combinations = generateCombinations(numberOfOccurrences, numberOfCombinations, definitions);

            String dirPath = System.getProperty("user.dir") + "/DAGS/" + "RESULT DAG " + DAGNum;
            treeDir = new File(dirPath);
            success = treeDir.mkdir();

            if(!success){
                System.err.println("UNABLE TO CREATE DIRECTORY 3213");
                System.exit(1);
            }

            stringNodes = convertToStringArr(nodeList);
            generateDAGForEachCombination(combinations, stringNodes);
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

    private ArrayList<OpNode> fillNodeArray(){
        Set<Integer> usedNodeNums = new HashSet<>();
        ArrayList<OpNode> nodeArray = new ArrayList<>();

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


    private void writeDefinedNodesToFile(Writer writer, ArrayList<OpNode> definedNodes) throws IOException {
        for (OpNode definedNode: definedNodes) {
            writer.write("\t" + definedNode.getNodeNum() + " [label=\"" + definedNode.getNodeName() +"\"" + "]\n");
        }

    }



    private int calculateNumberOfCombinations(ArrayList<definitionPair> defPars, HashMap<String, Integer> numberOfOccurrences){
        int result = 0;
        String variable = "";

        for(definitionPair pair : defPars){
            variable = pair.getVariable();
            if(numberOfOccurrences.containsKey(variable)){
                if(result == 0){
                    result = (int) Math.pow(pair.getDefinitions().length, numberOfOccurrences.get(variable));
                }else{
                    result = result * (int) Math.pow(pair.getDefinitions().length, numberOfOccurrences.get(variable));
                }
            }
        }
        return result;
    }

    private HashMap<String,Integer> calculateNumberOfOccurrencesForNodeName(ArrayList<OpNode> nodes){
        HashMap<String, Integer> numberOfOccurrences= new HashMap<>();
        int newValue;

        for (OpNode node: nodes) {
            if(numberOfOccurrences.containsKey(node.getNodeName())){
                newValue = numberOfOccurrences.get(node.getNodeName())+1;
                numberOfOccurrences.put(node.getNodeName(), newValue);
            }
            else{
                numberOfOccurrences.putIfAbsent(node.getNodeName(),1);
            }
        }

        return numberOfOccurrences;
    }

    //
    private ArrayList<ArrayList<String>> generateCombinations(HashMap<String, Integer> numberOfOccurrences, int loopIndex, ArrayList<definitionPair> defPairs){
        ArrayList<Integer> counter = new ArrayList<>();
        ArrayList<Integer> maxIndexes = new ArrayList<>();
        ArrayList<String[]> values = new ArrayList<>();
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        int counterIndex = 0;
        variableRep = new ArrayList<>();

        //Set MaxIndexes, set counter to 0 and add values to the value array.
        for(definitionPair defPair : defPairs){
            if(numberOfOccurrences.containsKey(defPair.getVariable())){
                for(int i = 0; i < numberOfOccurrences.get(defPair.getVariable()); i++){
                    counter.add(0);
                    variableRep.add(defPair.getVariable());

                    maxIndexes.add(defPair.getDefinitions().length-1);
                    values.add(defPair.getDefinitions());
                }
            }
        }

        //generate every possible combination of variables.
        for(int i = 0 ; i < loopIndex ; i++){
            if(counter.get(counterIndex) < maxIndexes.get(counterIndex)){
                addCurrentCombination(counter, values, result);
                counter.set(counterIndex, counter.get(counterIndex)+1);

            }else if(counter.get(counterIndex).equals(maxIndexes.get(counterIndex))){

                addCurrentCombination(counter, values, result);
                for(int j = counterIndex ; j < counter.size() ; j++){
                    if(counter.get(j).equals(maxIndexes.get(j))){
                        counter.set(j, 0);
                    }
                    else {
                        counter.set(j, counter.get(j)+1);
                        counterIndex = 0;
                        break;
                    }
                }
            }
        }

        return result;
    }

    private void addCurrentCombination(ArrayList<Integer> counter, ArrayList<String[]> values, ArrayList<ArrayList<String>> result){
        ArrayList<String> choices = new ArrayList<>();
        int resultIndex = 0;
        for(int index : counter){
            choices.add(values.get(resultIndex)[index]);
            resultIndex++;
        }
        result.add(choices);
    }

    private void generateDAGForEachCombination(ArrayList<ArrayList<String>> combinations, ArrayList<String> stringNodes) throws IOException{
        File file;
        int DAGNum = 0;
        String label;
        Writer writer;
        String definedStringNode;
        int i;
        for(ArrayList<String> combination : combinations){
            label = "definedDAG" + DAGNum + ".txt";
            file = new File(treeDir, label);
            DAGNum++;

            try(FileOutputStream fileStream = new FileOutputStream(file);
                DataOutputStream data0 = new DataOutputStream(fileStream)) {
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            writer = new FileWriter(file);
            initGraphFile(writer);
            writeEdgesToFile(writer, edges);
            i = 0;
            for(int j = 0 ; j < stringNodes.size() ; j++){
                if(stringNodes.get(j).contains(variableRep.get(i)) && i < combination.size()){
                    definedStringNode = stringNodes.get(j).replaceAll(variableRep.get(i), combination.get(i));
                    writer.write(definedStringNode);
                    i++;
                }
                else{
                    writer.write(stringNodes.get(j));
                }
            }

            writer.close();
        }
    }

    private ArrayList<String> convertToStringArr(ArrayList<OpNode> nodes){
        ArrayList<String> stringNodes= new ArrayList<>();
        for (OpNode node: nodes) {
            stringNodes.add("\t" + node.getNodeNum() + " [label=\"" + node.getNodeName() +"\"" + "]\n");
        }

        return stringNodes;
    }
}
