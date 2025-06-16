package forest;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputAdapter;

/**
 * 樹状整列におけるMVCのコントローラ（C）を担うクラスになります。
 */
public class ForestController extends MouseInputAdapter implements MouseWheelListener
{
    /**
     * 樹状整列におけるMVCのモデル（M）を記憶するフィールドです。
     */
    private ForestModel model;

    /**
     * 樹状整列におけるMVCのビュー（V）を記憶するフィールドです。
     */
    private ForestView view;

    /**
     * マウスの直近の位置を記憶するフィールドです。
     */
    private Point previous;

    /**
     * マウスの現在の位置を記憶するフィールドです。
     */
    private Point current;

    /**
     * このクラスのインスタンスを生成するコンストラクタです。
     */
    public ForestController()
    {
        super();
    }

    /**
     * マウスのボタンをクリックしたときに動作するメソッドです。 クリックした位置からノードを割り出します。
     */
    public void  mouseClicked(MouseEvent aMouseEvent)
    {
        //未実装
    }

    /**
     * マウスドラッグ（ボタンを押しながら移動）したときに動作するメソッドです。 直近の位置と現在の位置から移動量を割り出してスクロールします。
     */
    public void mouseDragged(MouseEvent aMouseEvent)
    {
        //未実装
    }

    /**
     * マウスがウィンドウに入ったときに動作するメソッドです。
     */
    public void mouseEntered(MouseEvent aMouseEvent)
    {
        //未実装
    }

    /**
     * マウスがウィンドウに出たときに動作するメソッドです。
     */
    public void mouseExited(MouseEvent aMouseEvent)
    {
        //未実装
    }

    /**
     * マウスがウィンドウ内でただ移動したときに動作するメソッドです。
     */
    public void mouseMoved(MouseEvent aMouseEvent)
    {
        //未実装
    }

    /**
     * マウスのボタンが押されたときに動作するメソッドです。
     */
    public void mousePressed(MouseEvent aMouseEvent)
    {
        //未実装
    }

    /**
     * マウスのボタンが離されたときに動作するメソッドです。
     */
    public void mouseReleased(MouseEvent aMouseEvent)
    {
        //未実装
    }

    /**
     * マウスのホイールが回されたときに動作するメソッドです。
     */
    public void mouseWheelMoved(MouseWheelEvent aMouseWheelEvent)
    {
        //未実装
    }

    /**
     * モデルを設定するメソッドです。
     */
    public void setModel(ForestModel aModel)
    {
        //未実装
    }

    /**
     * ビューを設定するメソッドです。
     */
    public void setView(ForestView aView)
    {
        //未実装
    }

    /**
     * 自分自身を文字列に変換するメソッドです。
     */
    @Override
    public java.lang.String toString()
    {
        StringBuffer aBuffer = new StringBuffer();
        Class<?> aClass = this.getClass();
        aBuffer.append(aClass.getName());
        aBuffer.append("[");
        aBuffer.append(this);
        aBuffer.append("]");
        return aBuffer.toString();
    }


}