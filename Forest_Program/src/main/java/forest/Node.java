package forest;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * 樹状整列におけるノード（節）を担うクラスになります。
 */
public class Node extends Component {
    
    /**
     * ノード名：ラベル文字列を記憶するフィールドです。
     */
    private String name;

    /**
     * ノードの場所（位置：座標）を記憶するフィールドです。
     */
    private Point location;

    /**
     * ノードの大きさ（幅と高さ）を記憶するフィールドです。
     */
    private Point extent;

    /**
     * 樹状整列する際のノードの状態を記憶するフィールドです。
     */
    private Integer status;

    /**
     * このクラスのインスタンスを生成するコンストラクタです。
     * @param aString ノード名：ラベル文字列
     */
    public Node(String aString)
    {
        this.name = aString;
        this.status = Constants.UnKnown;
        this.location = new Point(0,0);
        this.extent = new Point(0,0);
        this.extent.x = this.stringWidth(aString) + Constants.Margin.x * 2;
        this.extent.y = this.stringHeight(aString) + Constants.Margin.y * 2;
        return;
    }

    /**
     * ノード（節）を描画するメソッドです。
     * @param aGraphics グラフィクス（描画コンテクスト）
     */
    public void draw(Graphics aGraphics)
    {
        int x = this.location.x;
        int y = this.location.y;
        int dx = this.extent.x;
        int dy = this.extent.y;

        //背景色を塗りつぶす
        aGraphics.setColor(Constants.BackgroundColor);
        aGraphics.fillRect(x, y, dx, dy);

        //枠線を書く
        aGraphics.setColor(Constants.ForegroundColor);
        aGraphics.drawRect(x, y, dx, dy);

        //ノード名を書く
        FontMetrics aMetrics = aGraphics.getFontMetrics();
        aGraphics.setFont(Constants.DefaultFont);
        aGraphics.drawString(this.name, x+Constants.Margin.x, y + aMetrics.getAscent()+Constants.Margin.y);
        return;
    }

    /**
     * ノード（節）の描画領域を応答するメソッドです。
     * @return このノードの境界を表す {@code Rectangle} オブジェクト。
     */
    public Rectangle getBounds()
    {
        return new Rectangle(location.x, location.y, extent.x, extent.y);
    }

    /**
     * ノード（節）の大きさを応答するメソッドです。
     * @return ノードの大きさを表す {@code Point} オブジェクト（{@code x} が幅、{@code y} が高さ）。
     */
    public Point getExtent()
    {
        return extent;
    }

    /**
     * ノード（節）の位置を応答するメソッドです。
     * @return ノードの位置を表す {@code Point} オブジェクト。
     */
    public Point getLocation()
    {
        return location;
    }

    /**
     * ノード（節）の名前を応答するメソッドです。
     * @return ノードの名前を表す文字列。
     */
    public String getName()
    {
        return name;
    }

    /**
     * ノード（節）の状態を応答するメソッドです。
     * @return ノードの状態を表す {@code Integer} 値。
     * @see Constants#UnKnown
     * @see Constants#UnVisited
     * @see Constants#Visited
     */
    public Integer getStatus()
    {
        return status;
    }

    /**
     * ノード（節）の大きさを設定するメソッドです。
     * @param aPoint ノードの新しい大きさを表す {@code Point} オブジェクト（{@code x} が幅、{@code y} が高さ）。
     */
    public void setExtent(Point aPoint)
    {
        extent = new Point(aPoint.x + Constants.Margin.x*2, aPoint.y + Constants.Margin.y*2);
        return;
    }

    /**
     * ノード（節）の位置を設定するメソッドです。
     * @param aPoint ノードの位置（座標）
     */
    @Override
    public void setLocation(Point aPoint)
    {
        location = aPoint;
        return;
    }

    /**
     * ノード（節）の名前を設定するメソッドです。
     * @param aString ノードに設定する新しい名前を表す文字列。
     */
    @Override
    public void setName(String aString)
    {
        this.name = aString;
        return;
    }

    /**
     * ノード（節）の状態を設定するメソッドです。
     * @param anInteger ノードの状態
     * @see Constants#UnKnown
     * @see Constants#UnVisited
     * @see Constants#Visited
     */
    public void setStatus(Integer anInteger)
    {
        status = anInteger;
        return;
    }

    /**
     * 文字列の高さを応答するメソッドです。
     * @param string 文字列
     * @return 文字列の描画に必要な高さ（ピクセル単位）。
     */
    protected int stringHeight(String string)
    {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        g.setFont(Constants.DefaultFont);

        FontMetrics aMetrics = g.getFontMetrics();
        int height = aMetrics.getHeight();

        return height;
    }

    /**
     * 文字列の幅を応答するメソッドです。
     * @param string 文字列
     * @return 文字列の描画に必要な幅（ピクセル単位）。
     */
    protected int stringWidth(String string)
    {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        g.setFont(Constants.DefaultFont);

        FontMetrics aMetrics = g.getFontMetrics();
        int width = aMetrics.stringWidth(string);

        return width;
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
        aBuffer.append("[node=");
        aBuffer.append(this.name);
        aBuffer.append("]");
        return aBuffer.toString();
    }
}
