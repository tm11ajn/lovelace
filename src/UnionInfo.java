import java.util.ArrayList;

public class UnionInfo {
    private ArrayList<Integer> portNumbers= new ArrayList<>();
    private int rPorts;
    private int lPorts;

    public UnionInfo(String[] info){
        for (int i = 1; i < info.length; i++) {
            portNumbers.add(Integer.parseInt(info[i]));
        }

    }

}
