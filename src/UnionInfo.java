import java.util.ArrayList;

public class UnionInfo {
    private ArrayList<String> portNumbers= new ArrayList<>();
    private boolean increaseAll = false;

    public UnionInfo(String[] info){

        if(info[1].equals("+")){
            increaseAll = true;
        }

        for (int i = 1; i < info.length; i++) {
            portNumbers.add(info[i]);
        }

    }

    public ArrayList<String> getPortNumbers() {
        return portNumbers;
    }

    public boolean isIncreaseAll() {
        return increaseAll;
    }
}
