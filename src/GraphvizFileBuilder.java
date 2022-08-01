import java.io.File;
import java.util.ArrayList;

public class GraphvizFileBuilder {
    private File theDir;
    public GraphvizFileBuilder(){
        String currentDir = System.getProperty("user.dir") + "/DAGS";
        this.theDir = new File(currentDir);
    }

    public File createDAGDirectory(){


        if(theDir.exists()){
            boolean deleteSuccess = theDir.delete();
            if(!deleteSuccess){
                System.err.println("UNABLE TO DELETE PREVIOUS DAG DIRECTORY");
                System.exit(1);
            }
        }

        boolean success = theDir.mkdir();
        if(!success){
            System.err.println("UNABLE TO CREATE DIRECTORY");
            System.exit(1);
        }

        return theDir;
    }

    public void createDAGFile(ArrayList<Edge> edges, int DAGNum){
        File file;
    }
}
