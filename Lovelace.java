import java.util.ArrayList;
import java.util.HashMap;

public class Lovelace {
    public static void main(String[] args) {
        int treeDepth;

        inputCheck inputChecker = new inputCheck(args);
        String tree = args[0];

        inputChecker.runNumArgCheck();
        inputChecker.validateFile(args[1]);
        treeDepth = inputChecker.depthOfTree(tree);


        OperationParser opPars = new OperationParser(args[1]);

        ArrayList<Operation> opList = opPars.getOpList();

        test(tree);
    }

    private String recursiveBuild(ArrayList<Operation> opList, int depth, String tree){



        return null;
    }


    private HashMap<Integer, String> createRulePerDepthHashmap(ArrayList<Operation> opList, int depth, String tree){

        return null;
    }

    //TODO TÄNK MER
    private static void test(String tree){
        HashMap<Integer, String> depthOperationMapping= new HashMap<>();
        String[] arr = tree.split("\\(");
        for(int i = 0 ; i < arr.length ; i++){
            System.out.println(arr[i]);
            if(!arr[i].contains(")")){
                depthOperationMapping.put(i, arr[i]);
            }
        }

    }
}
