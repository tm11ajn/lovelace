import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Lovelace {

    public static void main(String[] args) {

        inputCheck inputChecker = new inputCheck(args);
        inputChecker.runNumArgCheck();
        Scanner scan;
        ArrayList<Edge> DAGEdges;
        File treeFile = inputChecker.CheckForValidFile(args[0]);
        File grammarFile = inputChecker.CheckForValidFile(args[1]);

        OperationParser opPars = new OperationParser(grammarFile);

        HashMap<String, Operation> operationHashMap = opPars.getOperationHashMap();
        DAGGenerator generator = new DAGGenerator(operationHashMap);
        TreeParser treeParser = new TreeParser();
        GraphvizFileBuilder graphBuild = new GraphvizFileBuilder();
        graphBuild.createDAGDirectory();

        int DAGNum = 1;



        System.out.println("THIS IS AFTER PARSING THE OPERATIONS:");
        try {
            String currentTree;
            scan = new Scanner(treeFile);
            ArrayList<TreeNode> treeNodes;
            while(scan.hasNextLine()){

                currentTree = scan.nextLine();
                treeParser.parseLine(currentTree);
                treeNodes = treeParser.getTreeNodes();

                System.out.println("DAG number: " + DAGNum);
                DAGEdges = generator.temp(treeNodes);
                graphBuild.createDAGFile(DAGEdges, DAGNum, currentTree);
                treeNodes.clear();
                DAGNum++;
            }
            scan.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

}
