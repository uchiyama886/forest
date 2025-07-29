package forest;

import utility.Condition;
import utility.ValueHolder;

import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
     * propagate メソッドで使用する実際のスリープ時間（ミリ秒）を記憶するフィールドです。
     * テスト時に上書き可能です。
     */
    private long actualSleepTick; // 追加

    /**
     * このクラスのインスタンスを生成するコンストラクタです。
     * デフォルトのスリープ時間を使用します。
     */
    public Forest()
    {
        nodes =  new ArrayList<>();
        branches = new ArrayList<>();
        bounds = new Rectangle();
        this.actualSleepTick = Constants.SleepTick; // 追加: デフォルト値を設定
    }

    /**
     * このクラスのインスタンスを生成するコンストラクタです。
     * テスト目的などで、propagate メソッドのスリープ時間を上書きできます。
     * @param testSleepTick propagate メソッドで使用するスリープ時間（ミリ秒）
     */
    public Forest(long testSleepTick) // 追加: テスト用コンストラクタ
    {
        this(); // デフォルトコンストラクタを呼び出す
        this.actualSleepTick = testSleepTick; // 指定されたスリープ時間で上書き
    }

    /**
     * ブランチ（枝）を追加するメソッドです。
     * @param aBranch ブランチ（枝）
     */
    public void addBranch(Branch aBranch)
    {
        branches.add(aBranch);
        return;
    }

    /**
     * ノード（節）を追加するメソッドです。
     * @param aNode ノード（節）
     */
    public void addNode(Node aNode)
    {
        nodes.add(aNode);
        return;
    }

    /**
     * 樹状整列するトップ（一番上位）のメソッドです。
     */
    public void arrange()
    {
        arrange(null);
        return;
    }

    /**
     * 樹状整列するセカンドレベル（二番階層）のメソッドです。
     * @param aModel モデル
     */
    public void arrange(ForestModel aModel)
    {
        // 既存の arrange(ForestModel) メソッドは、内部で propagate を呼ぶ際に
        // propagate(ForestModel aModel) のみを呼び出すため、その propagate が
        // 新しい actualSleepTick フィールドを使用するように修正します。
        // arrange() --> arrange(null)
        // arrange(ForestModel) --ここまでは既存の呼び出しフローを変えない

        nodes.forEach(node -> {
            node.setStatus(Constants.UnVisited);
        });
        ArrayList<Node> roots = this.sortNodes(this.rootNodes());
        AtomicInteger x = new AtomicInteger(0);
        AtomicInteger y = new AtomicInteger(0);
        Consumer<Node> aConsumer = (Node root) -> {
            Point newPoint = new Point(x.get(), y.get());
            // propagate のスリープ時間は、Forest インスタンスの actualSleepTick に依存するため、
            // arrange(Node, Point, ForestModel) の呼び出しはそのままにできます。
            Point subTreeBottomRight = arrange(root, newPoint, aModel);
            y.set(root.getExtent().y + subTreeBottomRight.y + Constants.Interval.y);
        };
        roots.forEach(aConsumer);
        return;
    }

    /**
     * 樹状整列する再帰レベル（N番階層）のメソッドです。
     * @param aNode ノード（このノードから再帰的にたどって下位のものたちも整列する）
     * @param aPoint ノードの位置（座標）
     * @param aModel モデル（nullのときはアニメーションを行わない）
     * @return 整列されたサブツリーの右下隅の座標。
     */
    protected Point arrange(Node aNode, Point aPoint, ForestModel aModel)
    {
        if(aNode.getStatus() == Constants.Visited) {return aPoint;} 
        aNode.setStatus(Constants.Visited);

        ArrayList<Node> subNodes = this.sortNodes(this.subNodes(aNode));

        int[] subX = {aPoint.x + aNode.getExtent().x + Constants.Interval.x};
        int[] nextY = {aPoint.y};

        int[] minY = {Integer.MAX_VALUE};
        int[] maxY = {Integer.MIN_VALUE};

        Consumer<Node> aConsumer = (Node sub) -> {
            Point[] subPoint = {new Point(0,0)};
            new Condition(() -> sub.getStatus() != Constants.Visited).ifTrue(() -> {
            
                subPoint[0] = new Point(subX[0], nextY[0]);
                sub.setLocation(subPoint[0]);
           
                this.propagate(aModel); // propagate が actualSleepTick を使用するようになったため、引数は変更なし
                
                Point next = arrange(sub, subPoint[0], aModel);

                int subBottom = next.y;
                int subTop = sub.getLocation().y;

                minY[0] = Math.min(minY[0], subTop);
                maxY[0] = Math.max(maxY[0], subBottom);

                nextY[0] = subBottom + Constants.Interval.y + sub.getExtent().y;
            });
            
        };
        subNodes.forEach(aConsumer);

        int[] y = {0};
        new Condition(() -> (!subNodes.isEmpty() && minY[0] != Integer.MAX_VALUE && maxY[0] != Integer.MIN_VALUE)).ifThenElse(() -> {    
            new Condition(() -> (subNodes.size() == 1)).ifThenElse(() -> {
                y[0] = subNodes.get(0).getLocation().y;
            }, () -> {
                y[0] = (minY[0] + maxY[0]) / 2;
            });
        }, () -> {
            y[0] = aPoint.y;
            minY[0] = aPoint.y;
            maxY[0] = aPoint.y;
;       });
        aNode.setLocation(new Point(aPoint.x, y[0]));
        this.propagate(aModel); // propagate が actualSleepTick を使用するようになったため、引数は変更なし

        return new Point(aPoint.x, maxY[0]);
    }

    /**
     * フォレスト（木・林・森・亜格子状の森）の領域（矩形）を応答するメソッドです。
     * @return フォレストの境界を表す {@code Rectangle} オブジェクト。
     * ノードが一つも存在しない場合は、幅と高さが0の矩形を返します。
     */
    public Rectangle bounds()
    {
        AtomicReference<Rectangle> result = new AtomicReference<>();

        Runnable notEmptyNodes = () -> {
            AtomicInteger minX = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger minY = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger maxX = new AtomicInteger(Integer.MIN_VALUE);
            AtomicInteger maxY = new AtomicInteger(Integer.MIN_VALUE);

            Consumer<Node> aConsumer = (Node aNode) -> {
                Point loc = aNode.getLocation();
                Point ext = aNode.getExtent();

                minX.set(Math.min(minX.get(), loc.x));
                minY.set(Math.min(minY.get(), loc.y));
                maxX.set(Math.max(maxX.get(), loc.x + ext.x));
                maxY.set(Math.max(maxY.get(), loc.y + ext.y));
            };
            nodes.forEach(aConsumer);

            result.set(new Rectangle(minX.get(), minY.get(), maxX.get() - minX.get(), maxY.get() - minY.get()));
        };

        new Condition(() -> nodes.isEmpty()).ifThenElse(() -> {
            result.set(new Rectangle(0, 0, 0, 0));
        }, notEmptyNodes);

        this.bounds = result.get();
        return this.bounds;
    }

    /**
     * フォレスト（木・林・森・亜格子状の森）を描画するメソッドです。
     * @param aGraphics 描画を行うための {@code Graphics} オブジェクト。
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
        return;
    }

    /**
     * フォレスト（木・林・森・亜格子状の森）の領域（矩形）を水に流す（チャラにする）メソッドです。
     */
    public void flushBounds()
    {
        AtomicInteger cnt = new AtomicInteger(1);
        Consumer<Node> aConsumer = (Node aNode) -> {
            aNode.setLocation(new Point(0, aNode.getExtent().y*cnt.getAndIncrement()));
        };
        nodes.forEach(aConsumer);
        this.bounds = null;
        return;
    }

    /**
     * チックタックの間、スリープし、モデルが変化した、と騒ぐ（広める：放送する）メソッドです。
     * @param aModel モデル
     */
    protected void propagate(ForestModel aModel)
    {
        new Condition(() -> aModel == null).ifTrue(() -> {return;});

        try {
            Thread.sleep(this.actualSleepTick); // 変更: actualSleepTick を使用
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        SwingUtilities.invokeLater(() -> aModel.changed());
        return;
    }

    /**
     * フォレストの根元（ルート）となるノード群を応答するメソッドです。
     * @return フォレスト内のルートノードの {@code ArrayList<Node>}。
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
     * @return ノード名でソートされた新しい {@code ArrayList<Node>}。元のコレクションは変更されません。
     */
    protected ArrayList<Node> sortNodes(ArrayList<Node> nodeCollection)
    {
        ArrayList<Node> sorted = new ArrayList<>(nodeCollection);
        sorted.sort(Comparator.comparing(Node::getName));
        return sorted;
    }

    /**
     * 引数で指定されたノードのサブノード群を応答するメソッドです。
     * @param aNode ノード
     * @return 指定されたノードの直接の子ノードの {@code ArrayList<Node>}。
     */
    public ArrayList<Node> subNodes(Node aNode)
    {
        ValueHolder<ArrayList<Node>> aSubNodes = new ValueHolder<>(new ArrayList<>());
        Consumer<Branch> aConsumer = (Branch aBranch) -> {
            new Condition(() ->  (aBranch.start() == aNode)).ifTrue(()-> {
                aSubNodes.get().add(aBranch.end());
            });
        };
        branches.forEach(aConsumer);
        return aSubNodes.get();
    }

    /**
     * 引数で指定されたノードのスーパーノード群を応答するメソッドです。
     * @param aNode 親ノードを検索する対象の子ノード。
     * @return 指定されたノードの直接の親ノードの {@code ArrayList<Node>}。
     */
    public ArrayList<Node> superNodes(Node aNode)
    {
        ValueHolder<ArrayList<Node>> aSuperNodes = new ValueHolder<>(new ArrayList<>());
        Consumer<Branch> aConsumer = (Branch aBranch) -> {
            new Condition(() ->  (aBranch.end() == aNode)).ifTrue(()-> {
                aSuperNodes.get().add(aBranch.start());
            });
        };
        branches.forEach(aConsumer);
        return aSuperNodes.get();
    }

    /**
     * ノード（節）群（たち）を記憶するフィールドを返すメソッドです。
     * @return ノード（節）群（たち）を記憶するフィールド {@code ArrayList<Node>}。
     */
    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    /**
     * ブランチ（枝）群（たち）を記憶するフィールドを返すメソッドです。
     * @return ブランチ（枝）群（たち）を記憶するフィールド {@code ArrayList<Branch>}
     */
    public ArrayList<Branch> getBranches() {
        return this.branches;
    }

    /**
     * 樹状整列したフォレスト（森）の領域（矩形）を記憶するフィールドを返すメソッドです。
     * @return 樹状整列したフォレスト（森）の領域（矩形）を記憶するフィールド {@code Rectangle}
     */
    public Rectangle getBounds() {
        return this.bounds;
    }

    /**
     * 自分自身を文字列に変換するメソッドです。
     * @return このオブジェクトを表す文字列。
     */
    @Override
    public String toString()
    {
        StringBuffer aBuffer = new StringBuffer();
        Class<?> aClass = this.getClass();
        aBuffer.append(aClass.getName());
        aBuffer.append("[bounds=");
        aBuffer.append(this.bounds);
        aBuffer.append("]");
        return aBuffer.toString();
    }

    /**
     * 指定された位置（座標）にノードが存在するかを調べるメソッドです。
     * @param aPoint 位置（モデル座標）
     * @return 指定された位置に存在する {@code Node} オブジェクト。存在しない場合は {@code null} を返します。
     */
    public Node whichOfNodes(Point aPoint)
    {
        AtomicReference<Node> result = new AtomicReference<>(null);
        Consumer<Node> aConsumer = (Node aNode) -> {
            Rectangle rect = new Rectangle(aNode.getLocation().x, aNode.getLocation().y, aNode.getExtent().x, aNode.getExtent().y);
            new Condition(() -> rect.contains(aPoint)).ifTrue(() -> {
                result.set(aNode);
            });
        };
        nodes.forEach(aConsumer);
        return result.get();
    }
}