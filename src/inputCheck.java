
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

    public File CheckForValidFile(String fileName){
        File treeFile = new File(fileName);

        if(!treeFile.isFile()){
            System.out.println("The file: " + fileName + " is not a valid file");
            System.exit(1);
        }

        return treeFile;
    }
}
