package forest;

import utility.Condition;
import utility.ValueHolder;

import java.util.function.Consumer;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 樹状整列におけるフォレスト（木・林・森・亜格子状の森）を担うクラスになります。
 */
public class Forest extends Object
{
    /**
     * ノード（節）群（たち）を記憶するフィールドです。
     */
    private ArrayList<Node> nodes;

    /**
     * ブランチ（枝）群（たち）を記憶するフィールドです。
     */
    private ArrayList<Branch> branches;

    /**
     * 樹状整列したフォレスト（森）の領域（矩形）を記憶するフィールドです。
     */
    private Rectangle bounds;

    /**
     * このクラスのインスタンスを生成するコンストラクタです。
     */
    public Forest()
    {
        nodes =  new ArrayList<>();
        branches = new ArrayList<>();
    }

    /**
     * ブランチ（枝）を追加するメソッドです。
     * @param aBranch ブランチ（枝）
     */
    public void addBranch(Branch aBranch)
    {
        branches.add(aBranch);
    }

    /**
     * ノード（節）を追加するメソッドです。
     * @param aNode ノード（節）
     */
    public void addNode(Node aNode)
    {
        nodes.add(aNode);
    }

    /**
     * 樹状整列するトップ（一番上位）のメソッドです。
     */
    public void arrange()
    {
        arrange(null);
    }

    /**
     * 樹状整列するセカンドレベル（二番階層）のメソッドです。
     * @param aModel モデル
     */
    public void arrange(ForestModel aModel)
    {
        ArrayList<Node> roots = this.sortNodes(this.rootNodes());
        AtomicInteger x = new AtomicInteger(0);
        AtomicInteger y = new AtomicInteger(0);
        Consumer<Node> aConsumer = (Node root) -> {
            Point newPoint = new Point(x.get(), y.get());
            Point subTreeBottomRight = arrange(root, newPoint, aModel);
            y.set(subTreeBottomRight.y + Constants.Interval.y);
        };
        roots.forEach(aConsumer);
    }

    /**
     * 樹状整列する再帰レベル（N番階層）のメソッドです。
     * @param aNode ノード（このノードから再帰的にたどって下位のものたちも整列する）
     * @param aPoint ノードの位置（座標）
     * @param aModel モデル（nullのときはアニメーションを行わない）
     */
    protected Point arrange(Node aNode, Point aPoint, ForestModel aModel)
    {
        ArrayList<Node> subNodes = this.subNodes(aNode);
        AtomicInteger cnt = new AtomicInteger(0);
        Consumer<Node> aConsumer = (Node sub) -> {
            Point newPoint = new Point(aPoint.x + aNode.stringWidth(aNode.getName()) + Constants.Interval.x, aPoint.y + aNode.stringHeight(aNode.getName())*cnt.getAndIncrement() + Constants.Interval.y);
            arrange(sub, newPoint, aModel);
        };
        subNodes.forEach(aConsumer);
        return aPoint;
    }

    /**
     * フォレスト（木・林・森・亜格子状の森）の領域（矩形）を応答するメソッドです。
     */
    public Rectangle bounds()
    {
        //未実装
        return this.bounds;
    }

    /**
     * フォレスト（木・林・森・亜格子状の森）を描画するメソッドです。
     */
    public void draw(java.awt.Graphics aGraphics)
    {
        //ノードを書く
        Consumer<Node> writeNodes = (Node node) -> {
            node.draw(aGraphics);
        };
        nodes.forEach(writeNodes);

        //枝を描く
        Consumer<Branch> writeBranchs = (Branch branch) -> {
            branch.draw(aGraphics);
        };
        branches.forEach(writeBranchs);
    }

    /**
     * フォレスト（木・林・森・亜格子状の森）の領域（矩形）を水に流す（チャラにする）メソッドです。
     */
    public void flushBounds()
    {
        //未実装
    }

    /**
     * チックタックの間、スリープし、モデルが変化した、と騒ぐ（広める：放送する）メソッドです。
     * @param aModel モデル
     */
    protected void propagate(ForestModel aModel)
    {
        //未実装
    }

    /**
     * フォレストの根元（ルート）となるノード群を応答するメソッドです。
     */
    public ArrayList<Node> rootNodes()
    {
        ArrayList<Node> roots = new ArrayList<>(nodes);
        Consumer<Branch> aConsumer = (Branch aBranch) -> {
            roots.remove(aBranch.end());
        };
        branches.forEach(aConsumer);
        return roots;
    }

    /**
     * 引数で指定されたノード群をノード名でソート（並び替えを）するメソッドです。
     * @param nodeCollection ノード群
     */
    protected ArrayList<Node> sortNodes(ArrayList<Node> nodeCollection)
    {
        nodeCollection.sort(Comparator.comparing(Node::getName));
        return nodeCollection;
    }

    /**
     * 引数で指定されたノードのサブノード群を応答するメソッドです。
     * @param aNode ノード
     */
    public ArrayList<Node> subNodes(Node aNode)
    {
        ValueHolder<ArrayList<Node>> aSubNodes = new ValueHolder<>(new ArrayList<>());
        Consumer<Branch> aConsumer = (Branch aBranch) -> {
            // Runnable truePassage = ()-> {aSubNodes.get().add(aBranch.end())};
            // Supplier<Boolean> aSupplier = () ->  (aBranch.start() == aNode);
            // ifTrue(aSupplier, truePassage);
            new Condition(() ->  (aBranch.start() == aNode)).ifTrue(()-> {
                aSubNodes.get().add(aBranch.end());
            });
        };
        branches.forEach(aConsumer);
        return aSubNodes.get();
    }

    /**
     * 引数で指定されたノードのスーパーノード群を応答するメソッドです。
     */
    public ArrayList<Node> superNodes(Node aNode)
    {
        ValueHolder<ArrayList<Node>> aSuperNodes = new ValueHolder<>(new ArrayList<>());
        Consumer<Branch> aConsumer = (Branch aBranch) -> {
            // Runnable truePassage = ()-> {aSubNodes.get().add(aBranch.end())};
            // Supplier<Boolean> aSupplier = () ->  (aBranch.start() == aNode);
            // ifTrue(aSupplier, truePassage);
            new Condition(() ->  (aBranch.end() == aNode)).ifTrue(()-> {
                aSuperNodes.get().add(aBranch.start());
            });
        };
        branches.forEach(aConsumer);
        return aSuperNodes.get();
    }

    /**
     * 自分自身を文字列に変換するメソッドです。
     */
    @Override
    public String toString()
    {
        StringBuffer aBuffer = new StringBuffer();
        Class<?> aClass = this.getClass();
        aBuffer.append(aClass.getName());
        aBuffer.append("[");
        aBuffer.append(this);
        aBuffer.append("]");
        return aBuffer.toString();
    }

    /**
     * 指定された位置（座標）にノードが存在するかを調べるメソッドです。
     * @param aPoint 位置（モデル座標）
     */
    public Node whichOfNodes(Point aPoint)
    {
        //未実装
        return null;
    }
}
