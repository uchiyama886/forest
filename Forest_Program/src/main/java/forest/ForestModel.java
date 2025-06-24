package forest;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import utility.*;


/**
 * 樹状整列におけるMVCのモデル（M）を担うクラスになります。
 */
public class ForestModel extends Object {
    
    /**
     * 自分の依存物（ビューたち）を記憶しておくフィールドです。
     */
    protected ArrayList<ForestView> dependants;

    /**
     * 樹状整列それ自身を記憶しておくフィールドです。
     */
    private Forest forest;

    /**
     * 樹状整列それ自身を画像にして記憶しておくフィールドです。
     */
    private BufferedImage picture;

    /**
     * このクラスのインスタンスを生成するコンストラクタです。
     * @param aFile 樹状整列データファイル
     */
    public ForestModel(java.io.File aFile)
    {
        super();
        forest = new Forest();
        dependants = new ArrayList<>();
        this.read(aFile);
    }

    /**
     * 依存するビューを依存物に登録（加味）します。
     * @param aView ビュー（Viewのインスタンス）
     */
    public void addDependent(ForestView aView)
    {
        dependants.add(aView);
    }

    /**
     * アニメーションを行うメソッドです。
     */
    public void animate()
    {
        
    }

    /**
     * 樹状整列を行うメソッドです。
     */
    public void arrange()
    {
        forest.arrange();
    }

    /**
     * 自分自身が変化したことを依存物たちに放送（updateを依頼）するメソッドです。
     */
    public void changed() 
    {
        this.dependants.forEach(view -> view.update());
    }

    /**
     * 樹状整列それ自身を応答するメソッドです。
     */
    public Forest forest()
    {
        return this.forest;
    }

    /**
     * 樹状整列それ自身を画像化したもの（ピクチャ：BufferedImage）を応答するメソッドです。
     */
    protected BufferedImage picture()
    {
        int width = this.forest.bounds().width;
        int height = this.forest.bounds().height;
        this.picture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = picture.createGraphics();
        forest.draw(g);
        return this.picture;
    }

    /**
     * 樹状整列データファイルから樹状整列それ自身を生成するメソッドです。
     * @param aFile 樹状整列データファイル
     */
    protected void read(File aFile)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(aFile))) 
        {
            HashMap<Integer, Node> nodeMap = new HashMap<>();
            boolean[] isNode = {false};
            boolean[] isBranch = {false};

            final String[] lineHolder = new String[1];
            new Condition(() -> {
                try {
                    lineHolder[0] = reader.readLine();
                    return lineHolder[0] != null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }).whileTrue(() -> {
                String line = lineHolder[0].trim();
                Runnable nodeContents = () -> {
                    String parts[] = line.split(",");
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1].trim();
                    Node aNode = new Node(name);
                    forest.addNode(aNode);
                    nodeMap.put(id, aNode);
                };
                Runnable branchContents = () -> {
                    String parts[] = line.split(",");
                    int startId = Integer.parseInt(parts[0].trim());
                    int endId = Integer.parseInt(parts[1].trim());
                    Node start = nodeMap.get(startId);
                    Node end = nodeMap.get(endId);
                    new Condition(() -> (start != null && end != null)).ifTrue(() -> {
                        forest.addBranch(new Branch(start, end));
                    });
                };
                new Condition.Switch()
                    .addCase(() -> line.isEmpty(), () -> {})
                    .addCase(() -> line.equalsIgnoreCase(Constants.TagOfNodes), () -> {
                        isNode[0] = true;
                        isBranch[0] = false;
                    })
                    .addCase(() -> line.equalsIgnoreCase(Constants.TagOfBranches), () -> {
                        isNode[0] = false;
                        isBranch[0] = true;
                    })
                    .defaultCase(() -> {
                        new Condition(() -> isNode[0]).ifTrue(nodeContents);
                        new Condition(() -> isBranch[0]).ifTrue(branchContents);
                    })
                    .evaluate();    
            });
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /** 
     * 樹状整列の根元（ルート）になるノードを探し出して応答するメソッドです。
    */
    public Node root()
    {
        ArrayList<Node> rootNodes = roots();
        AtomicReference<Node> result = new AtomicReference<>(rootNodes.get(0));
        new Condition(() -> rootNodes.isEmpty()).ifTrue(() -> {result.set(null);});
        return result.get();
    }

    /**
     *  樹状整列の根元（ルート）になるノードたちを探し出して応答するメソッドです。
     */
    public ArrayList<Node> roots()
    {
        return forest.rootNodes();
    }

    /**
     * 自分自身を文字列に変換するメソッドです。
     */
    @Override
    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        Class<?> aClass = this.getClass();
        aBuffer.append(aClass.getName());
        aBuffer.append("[");
        aBuffer.append(this);
        aBuffer.append("]");
        return aBuffer.toString();
    }
}
