import java.util.ArrayList;
import java.util.Collections;
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

        HashMap<String, Operation> operationHashMap = opPars.getOperationHashMap();


        //new Test(tree);

        //test(tree);
    }

    private String recursiveBuild(ArrayList<Operation> opList, int depth, String tree){



        return null;
    }


    private HashMap<Integer, String> createRulePerDepthHashmap(ArrayList<Operation> opList, int depth, String tree){

        return null;
    }

    //TODO TÄNK MER
    private void test(String tree){
        HashMap<Integer, String> depthOperationMapping= new HashMap<>();
        String[] arr = tree.split("\\(");

        ArrayList<String> opArr = new ArrayList<String>();
        Collections.addAll(opArr, arr);
        int i = 0;

        while(!opArr.get(0).contains(")")){
            depthOperationMapping.put(i, opArr.get(0));
            System.out.println("depth: " + i + " operation: " + opArr.get(0));
            opArr.remove(0);
            i++;
        }

        for(String operation : opArr){
            int depth = calculateDepth(i, operation);
            System.out.println("depth: " + depth + " operation: " + operation);
        }

        System.out.println(opArr);

        /*
        for(int i = 0 ; i < arr.length ; i++){
            System.out.println(arr[i]);
            if(!arr[i].contains(")")){
                depthOperationMapping.put(i, arr[i]);
            }
        }
         */

    }


    private int calculateDepth(int currentDepth, String op){
        int depth = currentDepth;
        for (int i = 0; i < op.length(); i++) {
            if (op.charAt(i) == ')') {
                depth--;
            }
        }

        return depth;
    }
}
