package forest;

import java.util.ArrayList;
import java.util.List;

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

    public void addChild(TreeNode child) {
        children.add(child);
    }
}
