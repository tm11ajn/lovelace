import java.util.ArrayList;
import java.util.HashMap;

public class Lovelace {
    public static void main(String[] args) {

        inputCheck inputChecker = new inputCheck(args);
        inputChecker.runNumArgCheck();
        String tree = args[0];

        inputChecker.runNumArgCheck();
        inputChecker.validateFile(args[1]);


        OperationParser opPars = new OperationParser(args[1]);

        HashMap<String, Operation> operationHashMap = opPars.getOperationHashMap();

        TreeParser treeParser = new TreeParser();
        treeParser.parseLine(args[0]);

        ArrayList<TreeNode> treeNodes = treeParser.getTreeNodes();

        for (TreeNode node: treeNodes) {

            if(!node.isRoot()){
                if(node.isLeaf()){
                    System.out.println("Leaf Node: " + node.getLabel() + " with parent: " + node.getParent().getLabel());
                }else{
                    System.out.println("Node: " + node.getLabel() + " with parent: " + node.getParent().getLabel());
                }

            }else System.out.println("Node: " + node.getLabel() + " is the root");

        }

        DAGGenerator generator = new DAGGenerator(treeNodes, operationHashMap);
        System.out.println("hello");
        generator.getPortFromChild();
        System.out.println("hello1");

        System.exit(0);
    }
}
