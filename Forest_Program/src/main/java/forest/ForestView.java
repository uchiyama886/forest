package forest;

import javax.swing.JPanel;
import java.awt.Point;
import java.awt.Graphics;
import java.lang.String;

/**
 * 樹状整列におけるMVCのビュー（V）を担うクラスになります。
 */
public class ForestView extends JPanel
{

    /**
     * 樹状整列におけるMVCのモデル（M）を記憶するフィールドです。
     */
    private ForestModel model;

    /**
     * 樹状整列におけるMVCのコントローラ（C）を記憶するフィールドです。
     */
    private ForestController controller;

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
        model = aModel;
    }

    /**
     * このパネル（ビュー）の描画が必要になったときに動作するメソッドです。
     * @param aGraphics グラフィクス（描画コンテクスト）
     */
    @Override
    public void paintComponent(Graphics aGraphics)
    {
        super.paintComponent(aGraphics);

        this.model.forest().draw(aGraphics);
    }

    /**
     * 相対スクロールを行うメソッドです。
     * @param aPoint 相対スクロール量（現在のスクロール量に対する差分）
     */
    public void scrollBy(Point aPoint)
    {
        //未実装
    }

    /**
     * 絶対スクロールを行うメソッドです。
     */
    public void scrollTo(Point aPoint)
    {
        //未実装
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
        //未実装
    }

    /**
     * 指定された位置（座標）にノードが存在するかを調べるメソッドです。
     * @param aPoint 位置（ビュー座標）
     */
    public Node whichOfNodes(Point aPoint)
    {
        //未実装
        return null;
    }
}
