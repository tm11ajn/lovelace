import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class TreeParser {

    public static final String SPLIT_REGEX = "(->)|[^\\\\]#";
    private ArrayList<TreeNode> treeNodes = new ArrayList<>();
    private int maxDepth = 0;


    public TreeNode parseLine(String line)
            throws IllegalArgumentException{

        String[] splitLine = line.split(SPLIT_REGEX);
        String treeLine = splitLine[0].trim();

        return buildTree(treeLine, 0);
    }

    private TreeNode buildTree(String rhs, int nOfChildren){

        if (!containsParsingCharacters(rhs)) {
            TreeNode tree = new TreeNode(rhs.trim());
            if(!treeNodes.contains(tree)){
                treeNodes.add(tree);
            }
            return tree;

        } else {
            int length = rhs.length();
            Stack<Character> parStack = new Stack<>();
            String currentString = "";

            TreeNode tree = null;
            TreeNode child = null;
            String treeString = null;
            LinkedList<TreeNode> children = new LinkedList<>();

            for (int i = 0; i < length; i++) {
                char c = rhs.charAt(i);

                if (c == '(') {
                    treeString = rhs.substring(0, i);
                    boolean done = false;
                    parStack.push(c);

                    while (!done) {
                        i++;
                        c = rhs.charAt(i);

                        if (c == '(') {
                            parStack.push(c);
                            if(parStack.size() > maxDepth){
                                setMaxDepth(parStack.size());
                            }
                        } else if (c == ')') {
                            parStack.pop();
                        }

                        if (parStack.empty()) {
                            done = true;
                        }

                        if (done || (c == ' ' &&
                                !currentString.isEmpty() &&
                                parStack.size() == 1)) {
                            TreeNode tempTree = buildTree(currentString, 0);

                            children.addLast(tempTree);
                            currentString = "";
                        } else {
                            currentString += c;
                        }
                    }
                }
            }

            int size = children.size();
            tree = buildTree(treeString, size);


            while (!children.isEmpty()) {
                tree.hasChild(true);
                child = children.pollFirst();
                child.setParent(tree);
                tree.addChild(child);
            }

            return tree;
        }
    }

    private boolean containsParsingCharacters(String line) {

        if (line.matches(".*\\(.*|.*\\).*|.*[^\\\\]#.*|.*->.*")) {
            return true;
        }

        return false;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public ArrayList<TreeNode> getTreeNodes() {
        return treeNodes;
    }
}
