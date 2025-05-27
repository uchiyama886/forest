package model;

public class Branch {
    private Node parent;
    private Node child;

    public Branch(Node parent, Node child) {
        this.parent = parent;
        this.child = child;
    }

    // ゲッター ----------------------------
    public Node getParent() {
        return parent;
    }
    public Node getChild() {
        return child;
    }
    //------------------------------------
}
