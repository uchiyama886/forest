package forest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith; // JUnit 4 で Mockito を使うために必要
import org.mockito.Mock; // モックオブジェクトを作成するためのアノテーション
import org.mockito.junit.MockitoJUnitRunner; // Mockito のランナー。@Mock を有効にする

import java.awt.Dimension; // Node の extent で使用される可能性のあるクラス
import java.awt.Point;     // Node の location で使用されるクラス
import java.awt.Graphics;  // draw メソッドの引数となるクラス

import static org.junit.Assert.*; // JUnit 4 のアサーションメソッドをインポート
import static org.mockito.Mockito.*; // Mockito のスタティックメソッド（when, verify, mock など）をインポート

/**
 * {@code Branch} クラスの単体テストクラスです。
 * JUnit 4 と Mockito を利用して、{@code Branch} の各メソッドが期待通りに動作するかを検証します。
 * {@code Node} や {@code Graphics} といった依存オブジェクトは Mockito でモック化します。
 */
@RunWith(MockitoJUnitRunner.class) // このテストクラスを MockitoJUnitRunner で実行するように指定します
public class BranchTest {

    // Mockito の @Mock アノテーションを使って、Node オブジェクトをモック化します。
    // これにより、実際の Node インスタンスを作成せず、テストに必要な振る舞いだけを定義できます。
    @Mock
    private Node startNode; // 枝の始点となるモックノード
    @Mock
    private Node endNode;   // 枝の終点となるモックノード

    private Branch branch; // テスト対象の Branch インスタンス

    /**
     * 各テストメソッドの実行前に呼び出されるセットアップメソッドです。
     * ここでテスト対象の {@code Branch} インスタンスと、それに渡すモック {@code Node} の
     * 振る舞いを初期設定します。
     */
    @Before
    public void setUp() {
        // startNode モックの振る舞いを定義します。
        // when(モックオブジェクト.メソッド()).thenReturn(返す値); の形式で、メソッドが呼び出された際の戻り値を指定します。
        when(startNode.getName()).thenReturn("StartNode"); // getName() が呼び出されたら "StartNode" を返す
        when(startNode.getLocation()).thenReturn(new Point(10, 20)); // getLocation() が呼び出されたら (10, 20) を返す
        when(startNode.getExtent()).thenReturn(new Dimension(50, 30)); // getExtent() が呼び出されたら 幅50, 高さ30 を返す

        // endNode モックの振る舞いを定義します。
        when(endNode.getName()).thenReturn("EndNode");
        when(endNode.getLocation()).thenReturn(new Point(100, 50));
        when(endNode.getExtent()).thenReturn(new Dimension(60, 40));

        // 設定したモックノードを使って、テスト対象の Branch インスタンスを生成します。
        branch = new Branch(startNode, endNode);
    }

    

    // テストメソッド

    /// コンストラクタのテスト
    /**
     * {@code Branch} クラスのコンストラクタが、引数として渡されたノードを
     * 正しく保持しているかをテストします。
     */
    @Test
    public void testConstructor() {
        // Branch インスタンスが null でないことを確認
        assertNotNull("Branch オブジェクトがnullであってはならない", branch);
        // 始点ノードが正しく設定されていることを確認
        assertEquals("始点ノードが正しく設定されていること", startNode, branch.start());
        // 終点ノードが正しく設定されていることを確認
        assertEquals("終点ノードが正しく設定されていること", endNode, branch.end());
    }

    ---

    ### `start()` メソッドのテスト
    /**
     * {@code start()} メソッドが、コンストラクタで設定された始点ノードを正しく返すかをテストします。
     */
    @Test
    public void testStartMethod() {
        assertEquals("start() メソッドが正しい始点ノードを返すこと", startNode, branch.start());
    }

    ---

    ### `end()` メソッドのテスト
    /**
     * {@code end()} メソッドが、コンストラクタで設定された終点ノードを正しく返すかをテストします。
     */
    @Test
    public void testEndMethod() {
        assertEquals("end() メソッドが正しい終点ノードを返すこと", endNode, branch.end());
    }

    ---

    ### `toString()` メソッドのテスト
    /**
     * {@code toString()} メソッドが、期待されるフォーマットで文字列を返すかをテストします。
     * Node の toString() も呼び出されるため、Node モックの getName() の戻り値が反映されることを確認します。
     */
    @Test
    public void testToStringMethod() {
        // Branch の toString() は "forest.Branch[start=Node[StartNode],end=Node[EndNode]]" のようになるはずです。
        // Node の toString() が "Node[名前]" の形式であると仮定しています。
        // ここでの startNode.toString() と endNode.toString() は、
        // Mockito で定義した name を元にした Node クラスの実際の toString() 挙動に依存します。
        // もし Node.toString() がモックできない場合（finalやstaticでない限り可能ですが）、
        // このテストは Node の toString() の実装に強く依存することになります。
        // 例示の Node クラスが Object の toString() のままであれば、異なる文字列になります。
        // Forestプロジェクトの Node クラスはtoString()がNode[node=name]の形式なのでそれに合わせます。
        String expectedString = "forest.Branch[start=forest.Node[node=StartNode],end=forest.Node[node=EndNode]]";
        assertEquals("toString() メソッドが期待される文字列を返すこと", expectedString, branch.toString());
    }

    ---

    ### `draw()` メソッドのテスト
    /**
     * {@code draw()} メソッドが、{@code Graphics} オブジェクトのメソッドを正しく呼び出すかをテストします。
     * 特に、線を描画するための {@code setColor} と {@code drawLine} が正しい引数で呼び出されるかを確認します。
     */
    @Test
    public void testDrawMethod() {
        // Graphics オブジェクトをモック化します。
        // 描画メソッドは Graphics オブジェクトの内部状態を変更するのではなく、外部とやり取りするためモック化が適しています。
        Graphics mockGraphics = mock(Graphics.class);

        // テスト対象の Branch の draw メソッドを呼び出します。
        branch.draw(mockGraphics);

        // Mockito の verify メソッドを使って、mockGraphics オブジェクトのメソッドが呼び出されたことを検証します。

        // 1. Constants.ForegroundColor で色が設定されたことを確認
        verify(mockGraphics).setColor(Constants.ForegroundColor);

        // 2. drawLine メソッドが正しい座標で呼び出されたことを確認します。
        // Branch.java の draw メソッドのロジックに基づいて計算します:
        // 始点ノードの右端中央:
        // fromX = startNode.getLocation().x + startNode.getExtent().width = 10 + 50 = 60
        // fromY = startNode.getLocation().y + startNode.getExtent().height / 2 = 20 + 30 / 2 = 35
        // 終点ノードの左端中央:
        // toX = endNode.getLocation().x = 100
        // toY = endNode.getLocation().y + endNode.getExtent().height / 2 = 50 + 40 / 2 = 70
        verify(mockGraphics).drawLine(60, 35, 100, 70);
    }
}
