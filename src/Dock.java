import java.util.ArrayList;

/**
 * Representation of a dock which includes dockNumber. Poorly written.
 *
 * @author Eric Andersson
 */

public class Dock {
    private int dockNum;
    private String arg = null;
    private ArrayList<String> args = new ArrayList<>();


    public Dock(String[] args){
        if(args.length >= 2){
            this.dockNum = Integer.parseInt(args[0]);
           // this.args = new String[args.length -1];
           // System.arraycopy(args, 1, this.args, 0 , args.length -1);
        }else{
            System.err.println("ERROR: Faulty Dock");
            System.exit(1);
        }
    }

    public Dock(int dockNum){

        this.dockNum = dockNum;
    }

    public void insertArg(String arg) {
        args.add(arg);
    }

    public int getDockNum() {
        return dockNum;
    }

    /*
    public String[] getArgs() {
        return args;
    }

     */

    public ArrayList<String> getArgs() {
        return args;
    }
}
