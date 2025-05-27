package model;

public class Tree {
    private int treeNum;
    private String name;

    public Tree(int no, String name) {
        this.treeNum = no;
        this.name = name;
    }

    // ゲッター　-------------------
    public int getTreeNum() {
        return treeNum;
    }

    public String getName() {
        return name;
    }
    //--------------------------
}
