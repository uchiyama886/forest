package forest;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.File;


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
        //未実装
    }

    /**
     * 依存するビューを依存物に登録（加味）します。
     * @param aView ビュー（Viewのインスタンス）
     */
    public void addDependent(ForestView aView)
    {
        //未実装
    }

    /**
     * アニメーションを行うメソッドです。
     */
    public void animate()
    {
        //未実装
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
        //未実装
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
        return this.picture;
    }

    /**
     * 樹状整列データファイルから樹状整列それ自身を生成するメソッドです。
     * @param aFile 樹状整列データファイル
     */
    protected void read(File aFile)
    {
        //未実装
    }

    /** 
     * 樹状整列の根元（ルート）になるノードを探し出して応答するメソッドです。
    */
    public Node root()
    {
        //未実装
        return null;
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
