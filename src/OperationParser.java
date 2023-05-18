import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class OperationParser {
    private final File operations;
    private final HashMap<String, Operation> operationHashMap = new HashMap<>();
    String nodeNames = "";

    private static final int OPERATION = 1;
    private static final int NODE = 2;
    private static final int EDGE_OR_DOCK = 3;
    private static final int UNION = 4;
    private static final int DOCK = 5;
    private static final int PORT = 6;
    private static final int CONTNODE = 7;
    private static final int CONTEXTRULE = 8;
    private int currentRow = 0;

    public OperationParser(File operationFile){
        this.operations = operationFile;

        try{
            ParseOps();

        }catch (FileNotFoundException e){
            System.out.println("cringe file");
        }
    }

    private void ParseOps() throws FileNotFoundException{
        Scanner scanner = new Scanner(operations);
        int mode = 0;
        int nodeNum = 0;
        int portNum, dockNum, fromNodeNumber, toNodeNumber, dockInternalID, nodeInternalID;
        Operation operation = null;
        OpNode currentOpNode = null;
        OpNode toNode, fromNode;
        HashMap<String, OpNode> currentOperationNodes = new HashMap<>();
        HashMap<Integer, OpNode> currentOperationNode2 = new HashMap<>();
        HashMap<Integer, Dock> currentOperationDocks = new HashMap<>();
        ArrayList<Edge> contextualEdgeList = new ArrayList<>();
        String[] portStrings;
        String[] nodeStrings;
        String nodeString, nodeName, arg;
        String[] unionInfo;
        String[] handlePort, handleEdge;
        Dock dock, dockFound;
        Edge edge;


        while(scanner.hasNextLine()){
            currentRow++;
            String line = scanner.nextLine();
            if(line.length() == 0) continue;
            mode = SetMode(line, mode);

            switch(mode){
                case OPERATION:
                    operation = createNewOperation(line);
                    //System.out.println("Operation created: " + operation.getOpName());
                    operationHashMap.put(operation.getOpName(), operation);
                    currentOperationNodes.clear();
                    currentOperationNode2.clear();
                    currentOperationDocks.clear();
                    break;

                case NODE:
                    //nodeStrings = trimAndSplit(line);
                    //currentOpNode = new OpNode(nodeStrings[1], nodeNum);
                    //nodeNames += currentOpNode.getNodeName();
                    //currentOperationNodes.put(currentOpNode.getNodeName(), currentOpNode);
                    //operation.addNode(currentOpNode);

                    nodeString = line.trim();
                    nodeName = nodeString.substring(nodeString.indexOf("\"")+1, nodeString.lastIndexOf("\""));
                    nodeInternalID = Integer.parseInt(nodeString.substring(0, nodeString.lastIndexOf("[")).trim());
                    System.out.println("node name: " + nodeName);
                    if(nodeString.contains(",")){
                        handlePort = nodeString.split(",");
                        portNum = Integer.parseInt(handlePort[1].substring(handlePort[1].indexOf("=")+1, handlePort[1].lastIndexOf("]")).trim());
                        currentOpNode = new OpNode(nodeName, nodeNum, portNum);

                    }else{
                        currentOpNode = new OpNode(nodeName, nodeNum);
                    }
                    currentOperationNodes.put(currentOpNode.getNodeName(), currentOpNode);
                    System.out.println("insert: " + currentOpNode.getNodeName() +" with internal ID: " + nodeInternalID);
                    currentOperationNode2.put(nodeInternalID, currentOpNode);
                    operation.addNode(currentOpNode);

                    nodeNum++;
                    break;
                case EDGE_OR_DOCK:
                    //if(!line.contains("edge")) addValidEdgeToOperation(operation, line, currentOperationNodes);

                    handleEdge = line.trim().split("->");
                    fromNodeNumber = Integer.parseInt(handleEdge[0].trim());
                    toNodeNumber = Integer.parseInt(handleEdge[1].substring(0, handleEdge[1].lastIndexOf("[")).trim());
                    arg = handleEdge[1].substring(handleEdge[1].indexOf("\"")+1, handleEdge[1].lastIndexOf("\"")).trim();

                    System.out.println("From node number: " + fromNodeNumber + " -> " + toNodeNumber + " with arg: " + arg);


                    if(currentOperationNode2.containsKey(fromNodeNumber)){
                        fromNode = currentOperationNode2.get(fromNodeNumber);
                        System.out.println("from node: " + fromNode.getNodeName()+"("+fromNode.getNodeNum()+")");
                        if(currentOperationNode2.containsKey(toNodeNumber)){
                            toNode = currentOperationNode2.get(toNodeNumber);

                            if(!toNode.getNodeName().equals("undef") && toNode.hasPort()){
                                operation.addEdge(new Edge(fromNode, toNode, arg));
                                operation.addEdgeInfo(fromNode, new EdgeInfo(toNode, arg));
                            }else if(!toNode.getNodeName().equals("undef")){
                                //TODO
                                operation.addEdgeInfo(fromNode, new EdgeInfo(toNode, arg));
                            }

                        }else if(currentOperationDocks.containsKey(toNodeNumber)){
                            System.out.println("CREATING DOCK");
                            dockFound = currentOperationDocks.get(toNodeNumber);
                            dockFound.insertArg(arg);
                            if(!fromNode.getDocks().containsKey(dockFound.getDockNum())){
                                System.out.println("DOCK ADDED");
                                fromNode.addDock(dockFound.getDockNum(), dockFound);
                            }
                            //fromNode.addDock(dockFound.getDockNum(), dockFound);
                        }else{
                            System.out.println("bad edge, unable to identify node or dock. Exit program");
                            System.exit(1);

                        }
                    }else{
                        System.out.println("Invalid edge: " + line);
                        System.exit(1);
                    }




                    break;
                case UNION:
                    //if(!line.contains("union")){
                        unionInfo = line.trim().split(" ");
                        //if(unionInfo.length == 0){
                        //    UnionInfo uInfo = new UnionInfo(unionInfo);
                        //    operation.addUnionInfo(uInfo);
                        //}
                    //System.out.println("UNION: operation name:" + operation.getOpName());
                        //TODO optimera, ändra UnionInfo så det räcker att skapa en UnionInfo
                        if(unionInfo.length == 2){
                            UnionInfo uInfo = new UnionInfo(unionInfo[0], true);
                            operation.addUnionInfo(uInfo);

                            UnionInfo uInfo2 = new UnionInfo(unionInfo[1], false);
                            operation.addUnionInfo(uInfo2);
                            operation.setUnion(true);
                        }
                    //}
                    break;
                case DOCK:
                    //if(!line.contains("DOCK")){
                    //    addDocks(line, currentOpNode);
                    //}
                    line = line.trim();
                    if(Character.isDigit(line.charAt(0))){
                        dockInternalID = Integer.parseInt(String.valueOf(line.charAt(0)));

                        //System.out.println("dock id =" + dockInternalID);
                        dockNum = Integer.parseInt(line.substring(line.indexOf("=")+1, line.lastIndexOf("]")).trim());
                        //System.out.println("dock number: " + dockNum);
                        //handleDock =
                        dock = new Dock(dockNum);
                        currentOperationDocks.put(dockInternalID, dock);
                    }else{
                        System.out.println("faulty dock: " + line);
                        System.exit(1);
                    }





                    break;
                case PORT:
                    portStrings = trimAndSplit(line);

                    if(currentOpNode != null){
                        currentOpNode.setPortNum(portStrings[1]);
                    }
                    break;

                case CONTEXTRULE:
                    //kommer nog inte behövas

                    break;

                default:
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
        line = line.toLowerCase();

        if(line.contains("operation")) {
            mode = 1;
            //}else if(line.contains("node")){
        }else if(checkForNode(line)){
            mode = 2;
        //}else if(line.contains("edges")) {
        //    mode = 3;
        }else if(line.contains("->")){
            mode = 3;

        //}else if(line.contains("union")){
        //    mode = 4;
        }else if(line.contains("dock")){
            mode = 5;
        }else if(line.contains("port")){
            mode = 6;
        }else if(checkForUnion(line)){
            mode = 4;
        }else{
            mode = 0;
        }

        return mode;
    }


    //verify if it is a node or not
    private boolean checkForNode(String line){
        //System.out.println("inside checkForNodes with line: " + line);
        if(!line.contains("->") && line.contains("label")){
            return true;
        }
        return false;
    }

    private boolean checkForUnion(String line){
        String[] lineArgs = trimAndSplit(line);

        if (lineArgs.length == 2) {

            return (lineArgs[0].length() == 1 && Character.isDigit(lineArgs[0].charAt(0)) &&
                    lineArgs[1].length() == 1 && Character.isDigit(lineArgs[1].charAt(0)));

        }else{
            return false;
        }
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
                operation.addEdgeInfo(fromNode, new EdgeInfo(toNode, edgeInfo[3]));
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

    private Operation createNewOperation(String line){
        Operation operation = null;

        String[] opArgs = trimAndSplit(line);
        if(opArgs.length == 3){
            //System.out.println("operation:" + line +" created");
            operation = new Operation(opArgs[1]);
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

    private String[] trimAndSplit(String line){
        line = line.trim();
        return line.split(" ");
    }
}
