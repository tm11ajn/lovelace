import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This program is used to convert multiple directed trees into directed acyclic graphs (DAG).
 * It is done by applying a graph extension grammar on the directed trees which combine and
 * restructure nodes of the tree into a DAG represented by the DOT language in a file. This file can
 * be executed Graphviz to visualize the DAG.
 */

public class Lovelace {

    public static void main(String[] args) {
        Scanner scan;
        ArrayList<Edge> DAGEdges;

        inputCheck inputChecker = new inputCheck(args);
        inputChecker.runNumArgCheck();

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
                if(!checkTreeBalance(currentTree)) continue;

                treeParser.parseLine(currentTree);
                treeNodes = treeParser.getTreeNodes();

                System.out.println("DAG number: " + DAGNum);
                DAGEdges = generator.temp(treeNodes);
                graphBuild.createDAGFile(DAGEdges, currentTree);
                treeNodes.clear();
                DAGNum++;
            }
            scan.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    /**
     * Check if there's an equal amount of left and right hand parentheses.
     * @param tree current tree
     */
    private static boolean checkTreeBalance(String tree){
        int balanced = 0;
        for (int i = 0; i < tree.length(); i++) {
            if(tree.charAt(i) == ')'){
                balanced++;
            }else if(tree.charAt(i) == '('){
                balanced--;
            }
        }

        if(balanced < 0){
            System.err.println("The amount of parentheses are not balanced for tree: " + tree +
                    ". The tree is missing " + balanced*(-1) + "x ')'.");
        }else if(balanced > 0){
            System.err.println("The amount of parentheses are not balanced for tree: " + tree +
                    ". The tree is missing " + balanced + "x '('.");
        }
        return balanced == 0;
    }
}
