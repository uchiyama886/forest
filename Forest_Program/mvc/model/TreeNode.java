package model;

import java.util.List;
import java.util.ArrayList;

//ツリー構造のクラス
public class TreeNode {
    private String name;
    private List<TreeNode> children;

    //コンストラクタ
    public TreeNode(String name) {
        this.name = name;
        this.children = new ArrayList<>(); 
    }

    //ゲッター -----------------------------
    public String getName() {
        return name;
    }

    public List<TreeNode> getChildren() {
        return children;
    }
    //-------------------------------------

    
}
