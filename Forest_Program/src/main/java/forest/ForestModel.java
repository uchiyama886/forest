package forest;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.File;
import mvc.Model;

/**
 * 樹上整列のモデル
 */
public class ForestModel extends Model {
    
    /**
     * このモデルに依存しているビューの一覧を保持する
     */
    protected ArrayList<ForestView> dependants;

    /**
     * モデルが保持しているファイルデータ
     */
    private File aFile;

    /**
     * コンストラクタ
     * 初期化メソッドを呼び出して dependants と aFile を初期化
     */
    public ForestModel()
    {
        super();
        this.initialize();
        return;
    }

    /**
     * コンストラクタ
     * ファイルを読み込んでモデルにセットする
     * @param aFile 読み込むファイル
     */
    public ForestModel(File aFile)
    {
        return;
    }

    private void initialize() {
    this.dependents = new ArrayList<>(); // 新しい空のリストを作成
    this.aFile = null; // ファイルはまだ読み込まれていない状態
    return;
    }
}