public class Dock {
    private int dockNum;
    private String[] arg;

    public Dock(int dockNum, String[] arg){
        this.dockNum = dockNum;
        this.arg = arg;

    }

    public int getDockNum() {
        return dockNum;
    }

    public String[] getArg() {
        return arg;
    }
}
