public class TreeNode {
    private String label;
    private TreeNode parent = null;
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

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
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
}
