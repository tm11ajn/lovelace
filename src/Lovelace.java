import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This program is used to convert multiple directed trees into directed acyclic graphs (DAG).
 * It is done by applying a graph extension grammar on the directed trees which combine and
 * restructure nodes of the tree into a DAG represented by the DOT language in a file. This file can
 * be executed Graphviz to visualize the DAG.
 */

public class Lovelace {
    private static final String TREE_FILE = "t";
    private static final String TREE_FILE_LONG = "trees";

    private static final String GRAMMAR_FILE = "g";
    private static final String GRAMMAR_FILE_LONG = "grammar";

    private static final String START_TREE_SIZE_INTERVAL = "L";
    private static final String END_TREE_SIZE_INTERVAL = "H";

    private static final String KEY_NODE_IN_TREE = "k";
    private static final String KEY_NODE_IN_TREE_LONG = "key";

    private static final String DEFINITION_FILE = "d";
    private static final String DEFINITION_FILE_LONG = "definition";

    public static void main(String[] args) {
        Scanner scan;
        File treeFile, grammarFile;
        File definitionFile = null;
        ArrayList<Edge> DAGEdges;
        int floor = 0;
        int roof = 0;
        int treeSize;
        String treeFileName = "";
        String grammarFileName = "";
        String keyNode = "";

        Options options = generateOptions();
        CommandLineParser cmdParser = new DefaultParser();
        inputCheck inputChecker = new inputCheck();
        definitionParser defPars;
        ArrayList<definitionPair> defPairs = new ArrayList<>();

        try{
            CommandLine commandLine = cmdParser.parse(options, args);
            treeFileName = commandLine.getOptionValue(TREE_FILE);
            grammarFileName = commandLine.getOptionValue(GRAMMAR_FILE);

            if(commandLine.hasOption(START_TREE_SIZE_INTERVAL)){
                /*
                floor = Integer.parseInt(commandLine.getOptionValue(START_TREE_SIZE_INTERVAL));
                if(floor < 0){
                    throw new NumberFormatException();
                }

                 */

                floor = calculateFloor(commandLine);
            }
            if(commandLine.hasOption(END_TREE_SIZE_INTERVAL)){
                roof = Integer.parseInt(commandLine.getOptionValue(END_TREE_SIZE_INTERVAL));
                if(roof < floor){
                    throw new NumberFormatException();
                }
            }

            if(commandLine.hasOption(DEFINITION_FILE)){
                 definitionFile = inputChecker.CheckForValidFile(commandLine.getOptionValue(DEFINITION_FILE));
                 defPars = new definitionParser(definitionFile);
                 defPairs = defPars.parseDefinitions();
            }

            if(commandLine.hasOption(KEY_NODE_IN_TREE)){
                keyNode = commandLine.getOptionValue(KEY_NODE_IN_TREE);
            }

        }catch (NumberFormatException e){
            System.err.println("The lower limit is below 0 or the upper limit is below the lower limit");
            System.exit(1);
        }catch(ParseException e){
            System.err.println("missing grammar file or tree file");
            System.exit(1);
        }

        treeFile = inputChecker.CheckForValidFile(treeFileName);
        grammarFile = inputChecker.CheckForValidFile(grammarFileName);

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
                treeSize = currentTree.split("[(]").length;
                System.out.println(currentTree + "with size: " + treeSize);
                if(!checkTreeBalance(currentTree) || !keyNode.isEmpty() && !currentTree.contains(keyNode)) continue;
                //Kolla if-satsen, ser konstig ut
                if(floor > treeSize || roof != 0 && treeSize > roof ) continue;

                treeParser.parseLine(currentTree);
                treeNodes = treeParser.getTreeNodes();

                System.out.println("DAG number: " + DAGNum);
                DAGEdges = generator.generateDAGEdges(treeNodes);
                graphBuild.createDAGFile(DAGEdges, currentTree, defPairs, DAGNum);
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

    private static int calculateFloor(CommandLine commandLine){
        int floor;

        floor = Integer.parseInt(commandLine.getOptionValue(START_TREE_SIZE_INTERVAL));
        if(floor < 0){
            throw new NumberFormatException();
        }

        return floor;
    }

    private static Options generateOptions(){
        Options options = new Options();
        Option lowLimitOpt = new Option(START_TREE_SIZE_INTERVAL,true,
                "lower limit of tree size");
        Option highLimitOpt = new Option(END_TREE_SIZE_INTERVAL, true,
                "High limit of tree size");
        Option keyNodeOpt = new Option(KEY_NODE_IN_TREE,KEY_NODE_IN_TREE_LONG, true,
                "A specific node that all generated DAGS must have");
        Option treeOpt = new Option(TREE_FILE, TREE_FILE_LONG, true,
                "The tree file which is used to generate DAGS");
        Option grammarOpt = new Option(GRAMMAR_FILE, GRAMMAR_FILE_LONG, true,
                "The file which is used to interpret the trees and make them into DAGS");
        Option definitionOpt = new Option(DEFINITION_FILE, DEFINITION_FILE_LONG, true,
                "definitions of variables");

        lowLimitOpt.setArgName("Low limit");
        highLimitOpt.setArgName("High limit");
        keyNodeOpt.setArgName("key node");
        treeOpt.setArgName("tree file");
        grammarOpt.setArgName("grammar file");
        definitionOpt.setArgName("definition file");

        treeOpt.setRequired(true);
        definitionOpt.setRequired(false);
        grammarOpt.setRequired(true);
        keyNodeOpt.setRequired(false);
        lowLimitOpt.setRequired(false);
        highLimitOpt.setRequired(false);

        options.addOption(lowLimitOpt);
        options.addOption(highLimitOpt);
        options.addOption(keyNodeOpt);
        options.addOption(treeOpt);
        options.addOption(grammarOpt);
        options.addOption(definitionOpt);

        return options;
    }
}
