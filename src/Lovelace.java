
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Lovelace {

    public static void main(String[] args) {

        inputCheck inputChecker = new inputCheck(args);
        inputChecker.runNumArgCheck();
        Scanner scan;
        File treeFile = inputChecker.validateTreeFile(args[0]);
        File grammarFile = inputChecker.validateTreeFile(args[1]);

        OperationParser opPars = new OperationParser(grammarFile);
        /*
        HashMap<String, Operation> operationHashMap = opPars.getOperationHashMap();
        DAGGenerator generator = new DAGGenerator(operationHashMap);
        TreeParser treeParser = new TreeParser();
        int DAGNum = 1;

        try {
            String currentTree;
            scan = new Scanner(treeFile);
            ArrayList<TreeNode> treeNodes;
            while(scan.hasNextLine()){

                currentTree = scan.nextLine();

                treeParser.parseLine(currentTree);
                treeNodes = treeParser.getTreeNodes();

                System.out.println("DAG number: " + DAGNum);
                DAGNum++;
                generator.getPortFromChild(treeNodes);
                treeNodes.clear();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         */

        System.exit(0);
    }

}
