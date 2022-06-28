
import java.io.File;


public class inputCheck {
    private String[] input;

    public inputCheck(String[] args){
        input = args;
    }

    public void runNumArgCheck(){
        if(input.length < 2){
            System.out.println("Missing arguments, please try again with the following format:\n" +
                    "java Lovelace.java <tree string> <operation file>");
            System.exit(1);
        }else if(input.length > 2){
            System.out.println("Too many arguments, please try again with the following format:\n" +
                    "java Lovelace.java <tree string> <operation file>");
            System.exit(1);
        }
    }

    public void validateFile(String operations){
        File grammar = new File(operations);
        if(!grammar.isFile()){
            System.out.println("The grammar file is not a valid file");
            System.exit(1);
        }
    }

    public int depthOfTree(String tree){
        int leftParentheses = 0;
        int rightParentheses = 0;
        for(int i = 0 ; i < tree.length() ; i++){
            if(tree.charAt(i) == '(') leftParentheses++;
            if(tree.charAt(i) == ')') rightParentheses++;
        }

        if(leftParentheses != rightParentheses){
            System.err.println("ERROR: The amount of left and right parentheses are not equal");
            System.exit(1);
        }

        return leftParentheses;
    }
}
