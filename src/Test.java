import java.util.*;


public class Test {
    private String tree;

    public Test(String tree){
        this.tree = tree;
    }


    private void test1(String tree){
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
