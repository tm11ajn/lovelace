import java.util.ArrayList;

public class TreeNode {
    private String label;
    private TreeNode parent = null;
    private ArrayList<TreeNode> children = new ArrayList<>();
    private int depth = 0;
    private boolean hasChild = false;

    public TreeNode(String label){
        this.label = label;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public TreeNode getParent() {
        return parent;
    }

    public String getLabel() {
        return label;
    }

    public void hasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public boolean isLeaf() {
        return !hasChild;
    }

    public boolean isRoot(){
        return parent == null;
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }
}
