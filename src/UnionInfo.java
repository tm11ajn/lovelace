import java.util.ArrayList;

public class UnionInfo {
    private final int numberOfPorts;
    private final boolean isLeft;

    public UnionInfo(String[] info){

        isLeft = info[0].equals("left") || info[0].equals("Left");

        numberOfPorts = Integer.parseInt(info[1]);

    }

    public int getNumberOfPorts() {
        return numberOfPorts;
    }

    public boolean isLeft() {
        return isLeft;
    }
}
