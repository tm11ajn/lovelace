import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class OperationParser {

    private File operations;
    private HashMap<String, Operation> operationHashMap = new HashMap<>();

    private static final int OPERATION = 1;
    private static final int NODE = 2;
    private static final int EDGE = 3;
    private static final int UNION = 4;
    private int currentRow = 0;

    public OperationParser(File operationFile){
        this.operations = operationFile;

        try{
            ParseOperations();
            System.out.println("@@@@@@@PRINTING OPERATIONS");
            for (Entry<String, Operation> operation: operationHashMap.entrySet()) {
                testPrintOperation(operation.getValue());
            }

        }catch (FileNotFoundException e){
            System.out.println("cringe file");
        }
    }

    /**
     * Scanning the operation file and creating operations
     * @throws FileNotFoundException
     */
    private void ParseOperations() throws FileNotFoundException {
        Scanner scanner = new Scanner(operations);
        int mode = 0;
        Operation operation = null;

        while(scanner.hasNextLine()){
            currentRow++;
            String line = scanner.nextLine();

            if(line.length() == 0) continue;

            mode = SetMode(line, mode);
            if(mode == OPERATION){
                operation = createOperation(line);
                operationHashMap.put(operation.getOpName(), operation);
            }else if(mode == NODE){
                if(!line.contains("nodes")) addValidNodesToOperation(operation, line);
            }else if(mode == EDGE){
                if(!line.contains("edge")) addValidEdgeToOperation(operation, line);
            }else if(mode == UNION){
                System.out.println("union");
            }
        }
        scanner.close();
    }

    /**
     * Choose mode depending on which keyword the line contains
     * @param line scanned line
     * @param mode previous mode
     * @return new mode
     */
    private int SetMode(String line, int mode){
        if(line.contains("OPERATION")){
            mode = 1;
        }else if(line.contains("nodes")){
            mode = 2;
        }else if(line.contains("edges")){
            mode = 3;
        }else if(line.contains("union")){
            mode = 4;
        }

        return mode;
    }

    private void testPrintOperation(Operation operation){
        System.out.println("Operation: " + operation.getOpName());

        System.out.println("\tNODES:");
        for (OpNode node: operation.getNodes()) {
            System.out.println("\t\tNode name: " + node.getNodeName());
            if(node.getDockNum() != -1){
                System.out.println("\t\t\tDOCK number:" + node.getDockNum());
            }
            if(node.getPortNum() != -1){
                System.out.println("\t\t\tPORT number:" + node.getPortNum());
            }
        }

        System.out.println("\tEDGES:");
        for (Edge edge : operation.getEdges()){
            System.out.println("\t\tFrom node " + edge.getFromNode() + " to node " + edge.getToNode() + " with label " +
                    edge.getLabel());
        }
    }

    private String[] trimAndSplit(String line){
        line = line.trim();
        return line.split(" ");
    }

    private void addValidEdgeToOperation(Operation operation, String line){
            String nonexNode;

        if(!line.contains("edge")) {

            String[] edgeInfo = trimAndSplit(line);

            if(!(edgeInfo.length == 4 && edgeInfo[1].equals("->"))){
                faultyEdgeFormat(line);
            }else if((nonexNode = validateNodesInEdge(edgeInfo[0], operation)) != null){
                referringToNonexistentNode(line, nonexNode);
            }else{
                operation.addEdge(new Edge(edgeInfo));
            }
        }
    }

    private void addValidNodesToOperation(Operation operation, String line){
        String[] nodeInfo = trimAndSplit(line);
        OpNode node = new OpNode(nodeInfo);
        operation.addNode(node);
    }

    private Operation createOperation(String line){
        Operation operation;

        line = line.trim();
        String opName = line.split(" ")[1];
        operation = new Operation(opName);
        return operation;
    }

    private String validateNodesInEdge(String fromNode, Operation operation){

        for (OpNode node: operation.getNodes()) {
            if(node.getNodeName().equals(fromNode)){
                return null;
            }
        }

        return fromNode;
    }

    private void referringToNonexistentNode(String line, String nonexNode){
        System.err.println("ERROR: At row " + currentRow + " the edge \"" + line.trim() +
                "\" is referring to the nonexistent node " + nonexNode);
        System.exit(1);
    }

    private void faultyEdgeFormat(String line){
        System.err.println("The edge \"" + line.trim() + "\" at row " + currentRow + " is not a valid edge");
        System.exit(1);

    }


    public HashMap<String, Operation> getOperationHashMap() {
        return operationHashMap;
    }
}
