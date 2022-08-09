
import java.io.File;

public class inputCheck {

    public inputCheck(){

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
