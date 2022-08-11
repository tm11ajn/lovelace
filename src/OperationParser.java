import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class OperationParser {

    private final File operations;
    private HashMap<String, Operation> operationHashMap = new HashMap<>();
    String nodeNames = "";

    private static final int OPERATION = 1;
    private static final int NODE = 2;
    private static final int EDGE = 3;
    private static final int UNION = 4;
    private static final int DOCK = 5;
    private static final int PORT = 6;
    private int currentRow = 0;

    public OperationParser(File operationFile){
        this.operations = operationFile;

        try{
            ParseOps();
            System.out.println("@@@@@@@PRINTING OPERATIONS");
            for (Entry<String, Operation> operation: operationHashMap.entrySet()) {
                testPrintOperation(operation.getValue());
            }

        }catch (FileNotFoundException e){
            System.out.println("cringe file");
        }
    }

    private void ParseOps() throws FileNotFoundException{
        Scanner scanner = new Scanner(operations);
        int mode = 0;
        int nodeNum = 0;
        Operation operation = null;
        OpNode currentOpNode = null;
        HashMap<String, OpNode> currentOperationNodes = new HashMap<>();
        String[] portStrings;
        String[] nodeStrings;
        String[] unionInfo;

        while(scanner.hasNextLine()){
            currentRow++;
            String line = scanner.nextLine();

            if(line.length() == 0) continue;

            mode = SetMode(line, mode);

            switch(mode){
                case OPERATION:
                    operation = createOperation(line);
                    operationHashMap.put(operation.getOpName(), operation);
                    currentOperationNodes.clear();
                    break;

                case NODE:
                    nodeStrings = trimAndSplit(line);
                    currentOpNode = new OpNode(nodeStrings[1], nodeNum);
                    nodeNames += currentOpNode.getNodeName();
                    currentOperationNodes.put(currentOpNode.getNodeName(), currentOpNode);
                    operation.addNode(currentOpNode);
                    nodeNum++;
                    break;
                case EDGE:
                    if(!line.contains("edge")) addValidEdgeToOperation(operation, line, currentOperationNodes);
                    break;
                case UNION:
                    if(!line.contains("union")){
                        System.out.println("inside union");
                        unionInfo = line.trim().split(" ");
                        if(unionInfo.length > 0){
                            UnionInfo uInfo = new UnionInfo(unionInfo);
                            operation.addUnionInfo(uInfo);
                        }
                    }
                    break;
                case DOCK:
                    if(!line.contains("DOCK")){
                        addDocks(line, currentOpNode);
                    }
                    break;
                case PORT:
                    portStrings = trimAndSplit(line);

                    if(currentOpNode != null){
                        currentOpNode.setPortNum(portStrings[1]);
                    }
                    break;
            }
        }
        scanner.close();
    }

    private void addDocks(String line, OpNode currentOpNode){
        String[] dockStrings = trimAndSplit(line);
        Dock dock = new Dock(dockStrings);
        if(currentOpNode != null){
            currentOpNode.addDock(dock.getDockNum(), dock);
        }
    }

    private int SetMode(String line, int mode){

        if(line.contains("OPERATION")){
            mode = 1;
        }else if(line.contains("node")){
            mode = 2;
        }else if(line.contains("edges")){
            mode = 3;
        }else if(line.contains("union")){
            mode = 4;
        }else if(line.contains("DOCK")){
            mode = 5;
        }else if(line.contains("PORT")){
            mode = 6;
        }

        return mode;
    }

    private void testPrintOperation(Operation operation){
        System.out.println("Operation: " + operation.getOpName());

        System.out.println("\tNODES:");
        HashMap<Integer, Dock> docks;

        for (OpNode node: operation.getNodes()) {
            System.out.println("\t\tNode name: " + node.getNodeName());
            docks = node.getDocks();

            if(!docks.isEmpty()){

                for(Dock dock : docks.values()){
                    System.out.print("\t\t\tDock number: " + dock.getDockNum() + " with args:");
                    if(dock.getArgs() != null){
                        for (String arg: dock.getArgs()) {
                            System.out.print(" " + arg);
                        }
                        System.out.print("\n");
                    }
                }
            }
            if(node.getPortNum() != -1){
                System.out.println("\t\t\tPORT number:" + node.getPortNum());
            }
        }

        System.out.println("\tEDGES:");
        for (Edge edge : operation.getEdges()){
            System.out.println("\t\tFrom node " + edge.getFromNode().getNodeName() + " to node " + edge.getToNode().getNodeName() + " with label " +
                    edge.getLabel());
        }
    }

    private String[] trimAndSplit(String line){
        line = line.trim();
        return line.split(" ");
    }

    private void addValidEdgeToOperation(Operation operation, String line, HashMap<String, OpNode> nodes){
            String nonexNode;

        if(!line.contains("edge")) {

            String[] edgeInfo = trimAndSplit(line);

            if(!(edgeInfo.length == 4 && edgeInfo[1].equals("->"))){
                faultyEdgeFormat(line);
            }else if((nonexNode = validateNodesInEdge(edgeInfo[0], operation)) != null){
                referringToNonexistentNode(line, nonexNode);
            }else{
                OpNode fromNode = nodes.get(edgeInfo[0]);
                OpNode toNode = nodes.get(edgeInfo[2]);
                operation.addEdge(new Edge(fromNode, toNode, edgeInfo[3]));
            }
        }
    }


    private Operation createOperation(String line){
        Operation operation = null;

        line = line.trim();
        String[] opArgs = line.split(" ");
        if(opArgs.length == 3){
            operation = new Operation(opArgs[1], opArgs[2]);
        }else{
            System.out.println(line);
            System.err.println("ERROR: incorrect number of argument in operation declaration");
            System.exit(1);
        }

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

    //TODO USE FOR VALIDATION
    public String getNodeNames() {
        return nodeNames;
    }
}
