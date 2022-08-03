import java.io.*;
import java.util.ArrayList;


public class GraphvizFileBuilder {
    private File theDir;
    private String dirPath;
    public GraphvizFileBuilder(){
        this.dirPath = System.getProperty("user.dir") + "/DAGS";
        this.theDir = new File(dirPath);
    }

    public File createDAGDirectory(){


        if(theDir.exists()){

            deleteDAGDir(theDir);
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

    public void createDAGFile(ArrayList<Edge> edges, int DAGNum, String currentTree){

        File file = new File(theDir, currentTree + ".txt");

        try(FileOutputStream fileStream = new FileOutputStream(file);
            DataOutputStream data0 = new DataOutputStream(fileStream)) {
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteDAGDir(File file){
        File[] files = file.listFiles();
        if(files != null){
            for (File delFile: files) {
                if(delFile.isDirectory()){
                    deleteDAGDir(delFile);
                }else{
                    delFile.delete();
                }
            }
        }
    }
}
