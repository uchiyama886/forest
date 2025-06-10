package mvc.model;

public class Tree {
    private int depth;
    private String name;

    public Tree(int no, String name) {
        this.treeNum = no;
        this.name = name;
    }

    // ゲッター　-------------------
    public int getDepth() {
        return depth;
    }

    public String getName() {
        return name;
    }
    //--------------------------

    public void show() {
        System.out.println(treeNum+", "+name);
    }
}
