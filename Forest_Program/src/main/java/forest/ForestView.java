package forest;

import javax.swing.JPanel;

import utility.Condition;

//import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Graphics;
import java.lang.String;

/**
 * 樹状整列におけるMVCのビュー（V）を担うクラスになります。
 */
public class ForestView extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * 樹状整列におけるMVCのモデル（M）を記憶するフィールドです。
     */
    private transient ForestModel model;

    /**
     * 樹状整列におけるMVCのコントローラ（C）を記憶するフィールドです。
     */
    private transient ForestController controller;

    /**
     * スクロール量（どこから描き出すのかを表す座標）を記憶するフィールドです。
     */
    private Point offset;

    /**
     * このクラスのインスタンスを生成するコンストラクタです。
     * @param aModel モデル（ForestModelのインスタンス）
     */
    public ForestView(ForestModel aModel)
    {
        super();
        this.model = aModel;
        this.model.addDependent(this);
        this.controller = new ForestController();
        this.controller.setModel(this.model);
        this.controller.setView(this);
        this.offset = new Point(0, 0);
        return;
    }

    /**
     * このパネル（ビュー）の描画が必要になったときに動作するメソッドです。
     * @param aGraphics グラフィクス（描画コンテクスト）
     */
    @Override
    public void paintComponent(Graphics aGraphics)
    {
        super.paintComponent(aGraphics);
        Integer width = this.getWidth();
        Integer height = this.getHeight();
        aGraphics.setColor(Constants.BackgroundColor);
		aGraphics.fillRect(0, 0, width, height);
		try { new Condition(() -> this.model == null).ifTrue(() -> { throw new RuntimeException(); }); }
		catch(RuntimeException anException) { return; }
		BufferedImage anImage = this.model.picture();
		try { new Condition(() -> anImage == null).ifTrue(() -> { throw new RuntimeException(); }); }
		catch(RuntimeException anException) { return; }
		aGraphics.drawImage(anImage, this.offset.x, this.offset.y, null);
		return;
    }

	/**
	 * スクロール量（offsetの逆向きの大きさ）を応答する。
	 * @return X軸とY軸のスクロール量を表す座標
	 */
	public Point scrollAmount()
	{
		Integer x = 0 - this.offset.x;
		Integer y = 0 - this.offset.y;
		return (new Point(x, y));
	}

    /**
     * 相対スクロールを行うメソッドです。
     * @param aPoint 相対スクロール量（現在のスクロール量に対する差分）
     */
    public void scrollBy(Point aPoint)
    {
        Integer x = this.offset.x + aPoint.x;
        Integer y = this.offset.y + aPoint.y;
        this.scrollTo(new Point(x, y));
        return;
    }

    /**
     * 絶対スクロールを行うメソッドです。
     */
    public void scrollTo(Point aPoint)
    {
        this.offset = aPoint;
        return;
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

    /**
     * 依存物として依存するものからの放送を受けたときに動作するメソッドです。 その際には再描画します。
     */
    public void update()
    {
        this.repaint();
        return;
    }

    /**
     * 指定された位置（座標）にノードが存在するかを調べるメソッドです。
     * @param aPoint 位置（ビュー座標）
     */
    public Node whichOfNodes(Point aPoint)
    {
        return model.forest().whichOfNodes(aPoint);
    }
}
