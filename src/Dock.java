public class Dock {
    private int dockNum;
    private String[] args;
    private String singleArg = null;

    public Dock(String[] args){


        if(args.length >= 2){
            this.dockNum = Integer.parseInt(args[0]);
            this.args = new String[args.length -1];
            System.arraycopy(args, 1, this.args, 0 , args.length -1);
        }else{
            System.err.println("ERROR: Faulty Dock");
            System.exit(1);
        }
    }

    public int getDockNum() {
        return dockNum;
    }

    public String[] getArgs() {
        return args;
    }

    public String getSingleArg() {
        return singleArg;
    }
}
