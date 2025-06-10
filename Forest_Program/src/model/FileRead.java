package mvc.model;

import java.io.*;
import java.util.List;
import java.util.ArrayList;


public class FileRead {
    private List<Tree> trees;
    private List<Node> nodes;
    private List<Branch> branches;

    public FileRead() {
        this.trees = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.branches = new ArrayList<>();
    }

    //ゲッター ---------------------------
    public List<Tree> getTrees() {
        return this.trees;
    }

    public List<Node> getNodes() {
        return this.nodes;
    }

    public List<Branch> getBranches() {
        return this.branches;
    }
    //-----------------------------------

    //ファイル読み込み
    public void read(File file) {
        boolean isTree = false;
        boolean isNode = false;
        boolean isBranch = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader.readLine()) != null) {
                switch(line.trim()) {
                    case "trees:":
                        isTree = true;
                        isNode = false;
                        isBranch = false;
                        break;
                    case "nodes:":
                        isTree = false;
                        isNode = true;
                        isBranch = false;
                        break;
                    case "branches:":
                        isTree = false;
                        isNode = false;
                        isBranch = true;
                        break;
                    default:
                        if(isTree) {
                            trees.add(this.RLE(line));
                        }
                        if(isNode) {
                            nodes.add(this.toNode(line));
                        }
                        if(isBranch) {
                            branches.add(this.toBranch(line));
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Tree RLE(String line) {
        int count = 0;
        String name = null;
        String[] parts = line.split(" ");
        for(String part: parts) {
            switch(part) {
                case "|--":
                    count++;
                    break;
                default:
                    name = part;
                    break;
            }
        }
        Tree node = new Tree(count, name);
        return node;
    }

    public Node toNode(String line) {
        String[] parts = line.split(",");
        int no = Integer.parseInt(parts[0]);
        String name = parts[1].stripLeading();
        Node node = new Node(no, name);
        return node;
    }

    public Branch toBranch(String line) {
        String[] parts = line.split(",");
        int parent = Integer.parseInt(parts[0]);
        int child = Integer.parseInt(parts[1].stripLeading());
        Node p=null, c=null;
        for(Node node: nodes) {
            if(parent == node.getNo()) {
                p = node;
            }
            if(child == node.getNo()) {
                c = node;
            }
            if(p != null && c != null) break;
        }
        Branch branch = new Branch(p, c);
        return branch;
    }
}
