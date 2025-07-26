package forest;

import java.awt.Graphics;

/**
 * 樹状整列におけるブランチ（枝）を担うクラスになります。
 */
public class Branch extends Object {
    /**
     * ブランチ（枝）の終点となるノードを記憶するフィールドです。
     */
    private Node end;

    /**
     * ブランチ（枝）の始点となるノードを記憶するフィールドです。
     */
    private Node start;

    /**
     * Branch クラスの新しいインスタンスを生成するコンストラクタです。
     * 枝の始点と終点を指定して、枝の関係を確立します。
     * @param from 枝の始点となる {@code Node} オブジェクト。
     * @param to 枝の終点となる {@code Node} オブジェクト。
     */
    public Branch(Node from, Node to) {
        super();
        this.start = from;
        this.end = to;
    }

    /**
     * このブランチ（枝）をグラフィックコンテキストに描画するメソッドです。
     * 枝は、始点ノードの右端中央から終点ノードの左端中央へと線で描画されます。
     * @param aGraphics 描画を行うための {@code Graphics} オブジェクト。
     */
    public void draw(Graphics aGraphics) {
        int fromX = this.start.getLocation().x + this.start.getExtent().x;
        int fromY = this.start.getLocation().y + this.start.getExtent().y / 2;
        int toX = this.end.getLocation().x;
        int toY = this.end.getLocation().y + this.end.getExtent().y / 2;

        aGraphics.setColor(Constants.ForegroundColor);
        aGraphics.drawLine(fromX, fromY, toX, toY);
        return;
    }

    /**
     * このブランチ（枝）の終点となるノードを応答するメソッドです。
     * @return この枝が指し示す終点となる {@code Node} オブジェクト。
     */
    public Node end() {
        return this.end;
    }

    /**
     * このブランチ（枝）の始点となるノードを応答するメソッドです。
     * @return この枝の開始点となる {@code Node} オブジェクト。
     */
    public Node start() {
        return this.start;
    }

     /**
     * この {@code Branch} オブジェクトの文字列表現を返します。
     * 通常は「クラス名[start=始点ノードの文字列表現,end=終点ノードの文字列表現]」の形式になります。
     * @return このオブジェクトを表す文字列。
     */
    @Override
    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        Class<?> aClass = this.getClass();
        aBuffer.append(aClass.getName());
        aBuffer.append("[start=");
        aBuffer.append(this.start);
        aBuffer.append("],[end=");
        aBuffer.append(this.end);
        aBuffer.append("]");
        return aBuffer.toString();
    }
}
