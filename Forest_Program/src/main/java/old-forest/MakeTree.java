package forest;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import utility.Condition;

/**
 * Tree構造を作成するクラス
 */
public class MakeTree {
    
    public static List<TreeNode> buildTree(List<Tree> treeData) {
        List<TreeNode> roots = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        // treeDataのデータをrootsとstackに追加する処理が入る?

        treeData.forEach(t -> {
            TreeNode current = new TreeNode(t.getName());
            int depth = t.getDepth();

            new Condition(() -> stack.size() > depth).whileTrue(() -> {
                stack.pop();
            });
            // 多分こういうこと↑
            // Supplier<Boolean> whilePassage = () -> { stack.size() > depth };
            // whileTrue(whilePassage, ()-> {
            //     stack.pop();  
            // };)

            new Condition(() -> stack.isEmpty).ifThenElse(() -> {
                roots.adChild(current);
            },()-> {
                stack.peak().addChild(current);
            });
            stack.push();

            // 多分こういうこと↑
            // Supplier<Boolean> ifPassage = () -> { stack.isEmpty() };
            // ifThenElse(ifPassage, () -> {
            //     roots.adChild(current);
            // };
            // ,() -> {
            //     stack.peak().addChild(current);
            // };)
            // stack.push();
       });

       return roots;
    }
    
}
