import java.util.ArrayList;

/**
 * UnionInfo includes information about the disjoint union operation in
 * graph extension grammar.
 *
 * @Eric Andersson
 */
public class UnionInfo {
    private final int numberOfPorts;
    private final boolean isLeft;

    public UnionInfo(String[] info){

        isLeft = info[0].equals("left") || info[0].equals("Left");

        numberOfPorts = Integer.parseInt(info[1]);

    }

    public UnionInfo(String stringPorts, boolean isLeft){
        this.isLeft = isLeft;
        this.numberOfPorts = Integer.parseInt(stringPorts);
        /*
        if(isLeft){
            System.out.println("Left with number of ports:" + this.numberOfPorts);
        }else{
            System.out.println("Right with number of ports:" + this.numberOfPorts);
        }

         */

    }

    public int getNumberOfPorts() {
        return numberOfPorts;
    }

    public boolean isLeft() {
        return isLeft;
    }
}
