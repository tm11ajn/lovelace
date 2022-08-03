import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


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

    public void createDAGFile(ArrayList<Edge> edges, int DAGNum, String currentTree) throws IOException {

        File file = new File(theDir, currentTree + ".txt");


        try(FileOutputStream fileStream = new FileOutputStream(file);
            DataOutputStream data0 = new DataOutputStream(fileStream)) {
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FileWriter writer = new FileWriter(file);
        initGraphFile(writer);
        writeEdgesToFile(writer, edges);
        writeNodesToFile(writer, edges);
        writer.write("}");
        writer.close();
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

    private void initGraphFile(Writer writer) throws IOException {
        writer.write("digraph G  {\n");
    }

    private void writeEdgesToFile(Writer writer, ArrayList<Edge> edges) throws IOException {
        for (Edge edge: edges) {
            writer.write("\t" + edge.getFromNode().getNodeNum() + " -> " + edge.getToNode().getNodeNum());
            writer.write(" [label=\"" + edge.getLabel() +"\"]\n");
        }
        writer.write("\n");

    }

    private void writeNodesToFile(Writer writer, ArrayList<Edge> edges) throws IOException {
        Set<Integer> usedNodeNums = new HashSet<>();
        for (Edge edge : edges){
            if(!usedNodeNums.contains(edge.getFromNode().getNodeNum())){
                writer.write("\t" + edge.getFromNode().getNodeNum() + " [label=\"" + edge.getFromNode().getNodeName() +"\"" + "]\n");
                usedNodeNums.add(edge.getFromNode().getNodeNum());
            }

            if(!usedNodeNums.contains(edge.getToNode().getNodeNum())){
                writer.write("\t" + edge.getToNode().getNodeNum() + " [label=\"" + edge.getToNode().getNodeName() +"\"" + "]\n");
                usedNodeNums.add(edge.getToNode().getNodeNum());
            }
        }
    }
}
