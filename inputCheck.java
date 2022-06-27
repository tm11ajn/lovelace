
import java.io.File;


public class inputCheck {
    private String[] input;

    public inputCheck(String[] args){
        input = args;
    }

    public boolean runNumArgCheck(){
        if(input.length == 0){
            System.out.println("Missing arguments, please try again with the following format:\n" +
                    "java Lovelace.java <tree string> <operation file>");
            return false;
        }else if(input.length > 2){
            System.out.println("Too many arguments, please try again with the following format:\n" +
                    "java Lovelace.java <tree string> <operation file>");
            return false;
        }
        else if(input.length == 1){
            System.out.println("Missing either the tree string or the operation file, " +
                    "please try again with the following format:\n" +
                    "java Lovelace.java <tree string> <operation file>");
        }

        return true;
    }

    public boolean validateFile(String operations){
        File grammar = new File(operations);
        if(!grammar.isFile()){
            System.out.println("The grammar file is not a valid file");
            return false;
        }

        return true;

    }
}
