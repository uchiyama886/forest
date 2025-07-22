package forest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith; // JUnit 4 Runner のために必要
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner; // JUnit 4 と Mockito の連携

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics;

import static org.junit.Assert.*; // JUnit 4 のアサーション
import static org.mockito.Mockito.*; // Mockito の static import

/**
 * Branch クラスの単体テスト。
 * JUnit 4 と Mockito を使用して実装されています。
 */
@RunWith(MockitoJUnitRunner.class) // MockitoJUnitRunner を使用してモックを自動初期化
public class BranchTest {

    // Mockito で自動的にモックが注入されるフィールド
    @Mock
    private Node startNode;
    @Mock
    private Node endNode;

    private Branch branch;

    /**
     * 各テストメソッドの前に実行される初期化メソッド。
     * Branch オブジェクトと、それに紐づく Node モックの振る舞いを設定します。
     */
    @Before
    public void setUp() {
        // startNode モックの振る舞いを定義
        when(startNode.getName()).thenReturn("StartNode");
        when(startNode.getLocation()).thenReturn(new Point(10, 20)); // 位置を設定
        when(startNode.getExtent()).thenReturn(new Dimension(50, 30)); // 大きさを設定

        // endNode モックの振る舞いを定義
        when(endNode.getName()).thenReturn("EndNode");
        when(endNode.getLocation()).thenReturn(new Point(100, 50)); // 位置を設定
        when(endNode.getExtent()).thenReturn(new Dimension(60, 40)); // 大きさを設定

        // Branch インスタンスを生成
        branch = new Branch(startNode, endNode);
    }

    /**
     * コンストラクタが正しくオブジェクトを初期化するかをテストします。
     */
    @Test
    public void testConstructor() {
        assertNotNull("Branch オブジェクトがnullであってはならない", branch);
        assertEquals("始点ノードが正しく設定されていること", startNode, branch.start());
        assertEquals("終点ノードが正しく設定されていること", endNode, branch.end());
    }

    /**
     * start() メソッドが正しい始点ノードを返すかをテストします。
     */
    @Test
    public void testStartMethod() {
        assertEquals("start() メソッドが正しい始点ノードを返すこと", startNode, branch.start());
    }

    /**
     * end() メソッドが正しい終点ノードを返すかをテストします。
     */
    @Test
    public void testEndMethod() {
        assertEquals("end() メソッドが正しい終点ノードを返すこと", endNode, branch.end());
    }

    /**
     * toString() メソッドが期待される文字列フォーマットで出力するかをテストします。
     */
    @Test
    public void testToStringMethod() {
        // Node の toString() は "Node[名前]" の形式であると仮定
        String expectedString = "forest.Branch[start=Node[StartNode],end=Node[EndNode]]";
        assertEquals("toString() メソッドが期待される文字列を返すこと", expectedString, branch.toString());
    }

    /**
     * draw() メソッドが Graphics オブジェクトの drawLine と setColor を適切に呼び出すかをテストします。
     */
    @Test
    public void testDrawMethod() {
        Graphics mockGraphics = mock(Graphics.class); // Graphics オブジェクトをモック化

        branch.draw(mockGraphics);

        // Constants.ForegroundColor で色が設定されることを検証
        verify(mockGraphics).setColor(Constants.ForegroundColor);

        // drawLine メソッドが正しい座標で呼び出されたことを検証
        // 始点ノードの中心座標: (location.x + extent.width/2, location.y + extent.height/2)
        // ここではブランチの描画ロジックが始点ノードの右端中央から終点ノードの左端中央へ線を描くことを想定
        // 始点ノードの右端中央: x = startNode.getLocation().x + startNode.getExtent().width = 10 + 50 = 60
        //                    y = startNode.getLocation().y + startNode.getExtent().height / 2 = 20 + 30 / 2 = 35
        // 終点ノードの左端中央: x = endNode.getLocation().x = 100
        //                    y = endNode.getLocation().y + endNode.getExtent().height / 2 = 50 + 40 / 2 = 70
        verify(mockGraphics).drawLine(60, 35, 100, 70);
    }
}
