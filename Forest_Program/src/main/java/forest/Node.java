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
    }

    /**
     * ノード（節）を描画するメソッドです。
     * @param aGraphics グラフィクス（描画コンテクスト）
     */
    public void draw(Graphics aGraphics)
    {
        //未実装
    }

    /**
     * ノード（節）の描画領域を応答するメソッドです。
     */
    public Rectangle getBounds()
    {
        return new Rectangle(location.x, location.y, extent.x, extent.y);
    }

    /**
     * ノード（節）の大きさを応答するメソッドです。
     */
    public Point getExtent()
    {
        return extent;
    }

    /**
     * ノード（節）の位置を応答するメソッドです。
     */
    public Point getLocation()
    {
        //未実装
        return location;
    }

    /**
     * ノード（節）の名前を応答するメソッドです。
     */
    public String getName()
    {
        return name;
    }

    /**
     * ノード（節）の状態を応答するメソッドです。
     */
    public Integer getStatus()
    {
        return status;
    }

    /**
     * ノード（節）の大きさを設定するメソッドです。
     * @param ノードの大きさ（幅と高さ）
     */
    public void setExtent(Point aPoint)
    {
        extent = aPoint;
    }

    /**
     * ノード（節）の位置を設定するメソッドです。
     * @param aPoint ノードの位置（座標）
     */
    @Override
    public void setLocation(Point aPoint)
    {
        location = aPoint;
    }

    /**
     * ノード（節）の名前を設定するメソッドです。
     */
    @Override
    public void setName(String aString)
    {
        this.name = aString;
    }

    /**
     * ノード（節）の状態を設定するメソッドです。
     * @param anInteger ノードの状態
     */
    public void setStatus(Integer anInteger)
    {
        status = anInteger;
    }

    /**
     * 文字列の高さを応答するメソッドです。
     * @param string 文字列
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
     */
    @Override
    public String toString()
    {
        //未実装
        return null;
    }
}
