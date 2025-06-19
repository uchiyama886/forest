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
     * このクラスのインスタンスを生成するコンストラクタです。
     */
    public Branch(Node from, Node to) {
        super();
        this.start = from;
        this.end = to;
    }

    /**
     * ブランチ（枝）を描画するメソッドです。
     */
    public void draw(Graphics aGraphics) {
        int fromX = this.start.getX() + this.start.getWidth();
        int fromY = (this.start.getY() + this.start.getHeight()) / 2;
        int toX = this.end.getX();
        int toY = (this.end.getY() + this.end.getHeight()) / 2;

        aGraphics.setColor(Constants.ForegroundColor);
        aGraphics.drawLine(fromX, fromY, toX, toY);
    }

    /**
     * ブランチ（枝）の終点となるノードを応答するメソッドです。
     */
    public Node end() {
        return this.end;
    }

    /**
     * ブランチ（枝）の始点となるノードを応答するメソッドです。
     */
    public Node start() {
        return this.start;
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
