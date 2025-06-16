package model;

import utility.*;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;

public class MakeTree {
    
    public static List<TreeNode> buildTree(List<Tree> treeData) {
        List<TreeNode> roots = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();

        treeData.forEach(t -> {
            TreeNode current = new TreeNode(t.getName());
            int depth = t.getDepth();

            Supplier<Boolean> whilePassage = () -> { stack.size() > depth };
            whileTrue(whilePassage, ()-> {
                stack.pop();  
            };)

            Supplier<Boolean> ifPassage = () -> { stack.isEmpty() };
            ifThenElse(ifPassage, () -> {
                roots.adChild(current);
            };
            ,() -> {
                stack.peak().addChild(current);
            };)
            stack.push();
       });

       return roots;
    }
    
}
