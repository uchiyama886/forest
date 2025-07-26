package forest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith; // JUnit 4 で Mockito を使うために必要
import org.mockito.Mock;         // モックオブジェクトを作成するためのアノテーション
import org.mockito.junit.MockitoJUnitRunner; // Mockito のランナー。@Mock を有効にする

import java.awt.Dimension;   // Node の getExtent() で返す Point の代わりに利用
import java.awt.FontMetrics; // draw メソッド内で Graphics.getFontMetrics() が返すオブジェクト
import java.awt.Graphics;    // draw メソッドの引数
import java.awt.Point;       // 位置や大きさを表すクラス
import java.awt.Rectangle;   // 境界を表すクラス

import static org.junit.Assert.*;       // JUnit 4 のアサーションメソッドをインポート
import static org.mockito.Mockito.*;     // Mockito のスタティックメソッド（when, verify, mock など）をインポート

/**
 * {@code Node} クラスの単体テストクラスです。
 * JUnit 4 と Mockito を利用して、{@code Node} の各メソッドが期待通りに動作するかを検証します。
 * {@code draw} メソッドのテストでは {@code Graphics} と {@code FontMetrics} をモック化します。
 * <p>
 * 注: {@code stringWidth} および {@code stringHeight} メソッドは {@code BufferedImage} と
 * {@code Graphics} オブジェクトを内部で生成するため、これらのメソッドの直接的なモック化は
 * 通常難しく、テストでは実際の動作に依存します。
 * そのため、これらのテストは簡易的なものになります。
 */
@RunWith(MockitoJUnitRunner.class) // このテストクラスを MockitoJUnitRunner で実行するように指定します
public class NodeTest {

    private String testNodeName; // テストで使用するノード名
    private Node node;           // テスト対象の Node インスタンス

    /**
     * 各テストメソッドの実行前に呼び出されるセットアップメソッドです。
     * ここでテスト対象の {@code Node} インスタンスを初期化します。
     */
    @Before
    public void setUp() {
        testNodeName = "TestNode";
        // Node のコンストラクタは stringWidth/Height を内部で呼び出すため、
        // これらのメソッドをモックすることはできません。
        // そのため、実際の Node インスタンスを生成します。
        node = new Node(testNodeName);
    }



    // テストメソッド

    // コンストラクタのテスト
    /**
     * {@code Node} クラスのコンストラクタが、引数として渡された名前と初期状態を
     * 正しく設定し、位置と大きさを初期化しているかをテストします。
     */
    @Test
    public void testConstructor() {
        // Node インスタンスが null でないことを確認
        assertNotNull("Node オブジェクトがnullであってはならない", node);
        // ノード名が正しく設定されていることを確認
        assertEquals("ノード名が正しく設定されていること", testNodeName, node.getName());
        // 初期状態が Constants.UnKnown であることを確認
        assertEquals("初期状態がConstants.UnKnownであること", Constants.UnKnown, (int)node.getStatus()); // Integer の比較に注意
        // 初期位置が (0,0) であることを確認
        assertEquals("初期位置が(0,0)であること", new Point(0, 0), node.getLocation());

        // コンストラクタで計算される extent (幅と高さ) が 0 より大きいことを確認
        // マージンとフォントサイズによって計算されるため、具体的な値ではなく「正の値」であることをテスト
        assertTrue("幅が0より大きいこと", node.getExtent().x > 0);
        assertTrue("高さが0より大きいこと", node.getExtent().y > 0);
    }

    

    // `getLocation()` / `setLocation()` のテスト
    /**
     * {@code getLocation()} メソッドが正しい位置を返すこと、および
     * {@code setLocation()} メソッドがノードの位置を正しく更新することをテストします。
     */
    @Test
    public void testGetAndSetLocation() {
        Point expectedLocation = new Point(10, 20);
        node.setLocation(expectedLocation); // 位置を設定
        assertEquals("設定された位置が正しく取得できること", expectedLocation, node.getLocation()); // 取得して確認

        Point newLocation = new Point(50, 60);
        node.setLocation(newLocation);
        assertEquals("setLocation() でノードの位置が更新されること", newLocation, node.getLocation());
    }

    

    // `getName()` / `setName()` のテスト
    /**
     * {@code getName()} メソッドが正しいノード名を返すこと、および
     * {@code setName()} メソッドがノード名を正しく更新することをテストします。
     */
    @Test
    public void testGetAndSetName() {
        assertEquals("getName() が正しいノード名を返すこと", testNodeName, node.getName());

        String newName = "NewNodeName";
        node.setName(newName); // 名前を設定
        assertEquals("setName() でノード名が更新されること", newName, node.getName()); // 取得して確認
    }

    

    // `getExtent()` / `setExtent()` のテスト
    /**
     * {@code getExtent()} メソッドが正しい大きさを返すこと、および
     * {@code setExtent()} メソッドがノードの大きさをマージンを考慮して正しく更新することをテストします。
     */
    @Test
    public void testGetAndSetExtent() {
        Point initialExtent = node.getExtent();
        assertNotNull("初期 extent がnullではないこと", initialExtent);
        assertTrue("初期 extent の幅と高さが正の値であること", initialExtent.x > 0 && initialExtent.y > 0);

        Point newRawExtent = new Point(100, 50); // マージンが加算される前の「生の」大きさ
        node.setExtent(newRawExtent); // 大きさを設定

        // setExtent は Constants.Margin を考慮して extent を更新するため、期待値もそれを考慮します
        Point expectedExtent = new Point(newRawExtent.x + Constants.Margin.x * 2, newRawExtent.y + Constants.Margin.y * 2);
        assertEquals("setExtent() でノードの大きさがマージンを考慮して更新されること", expectedExtent, node.getExtent());
    }

    

    // `getStatus()` / `setStatus()` のテスト
    /**
     * {@code getStatus()} メソッドが正しい状態を返すこと、および
     * {@code setStatus()} メソッドがノードの状態を正しく更新することをテストします。
     */
    @Test
    public void testGetAndSetStatus() {
        assertEquals("初期状態がConstants.UnKnownであること", Constants.UnKnown, (int)node.getStatus());

        node.setStatus(Constants.Visited); // 状態を設定
        assertEquals("setStatus() でノードの状態が更新されること", Constants.Visited, (int)node.getStatus());

        node.setStatus(Constants.UnVisited); // 別の状態に更新
        assertEquals("setStatus() でノードの状態がさらに更新されること", Constants.UnVisited, (int)node.getStatus());
    }

    

    // `getBounds()` メソッドのテスト
    /**
     * {@code getBounds()} メソッドがノードの正しい描画領域（{@code Rectangle}）を返すかをテストします。
     * これは位置と大きさから計算されるため、これらの setter/getter と連携して機能することを検証します。
     */
    @Test
    public void testGetBounds() {
        Point location = new Point(10, 20);
        node.setLocation(location); // 位置を設定

        // extent はコンストラクタで計算済み、または setExtent で設定されたものを使用
        Point extent = node.getExtent();

        Rectangle expectedBounds = new Rectangle(location.x, location.y, extent.x, extent.y);
        assertEquals("getBounds() が正しいRectangleを返すこと", expectedBounds, node.getBounds());
    }

    

    // `toString()` メソッドのテスト
    /**
     * {@code toString()} メソッドが、期待される文字列フォーマットで出力するかをテストします。
     */
    @Test
    public void testToString() {
        // Node の toString() は "forest.Node[node=ノード名]" の形式であると想定
        String expectedString = "forest.Node[node=" + testNodeName + "]";
        assertEquals("toString() が期待される文字列を返すこと", expectedString, node.toString());
    }

    

    // `draw()` メソッドのテスト
    /**
     * {@code draw()} メソッドが {@code Graphics} オブジェクトのメソッド（色設定、塗りつぶし、枠線、文字列描画）を
     * 適切に呼び出すかをテストします。
     * {@code Graphics} と {@code FontMetrics} はモック化して、その呼び出しを検証します。
     */
    @Test
    public void testDraw() {
        // Graphics オブジェクトをモック化
        Graphics mockGraphics = mock(Graphics.class);
        // FontMetrics オブジェクトもモック化し、Graphics.getFontMetrics() がこれを返すように設定
        FontMetrics mockFontMetrics = mock(FontMetrics.class);

        // Graphics.getFontMetrics() が呼び出されたら、モックの FontMetrics を返すように設定
        when(mockGraphics.getFontMetrics()).thenReturn(mockFontMetrics);
        // FontMetrics.getAscent() が呼び出されたら、適当なアセント値（例: 10）を返すように設定
        // これは drawString の Y 座標計算で使われるため重要です。
        when(mockFontMetrics.getAscent()).thenReturn(10);

        // ノードの位置をテスト用に設定
        node.setLocation(new Point(10, 20));

        // draw メソッドを呼び出します
        node.draw(mockGraphics);

        // Mockito の verify メソッドを使って、mockGraphics オブジェクトのメソッドが期待通りに呼び出されたことを検証します。

        // 1. 背景色が設定され、fillRect が正しい引数で呼び出されたことを確認
        verify(mockGraphics).setColor(Constants.BackgroundColor);
        verify(mockGraphics).fillRect(node.getLocation().x, node.getLocation().y, node.getExtent().x, node.getExtent().y);

        // 2. 前景色が設定され、drawRect が正しい引数で呼び出されたことを確認
        verify(mockGraphics).setColor(Constants.ForegroundColor);
        verify(mockGraphics).drawRect(node.getLocation().x, node.getLocation().y, node.getExtent().x, node.getExtent().y);

        // 3. フォントが設定され、drawString が正しい引数で呼び出されたことを確認
        verify(mockGraphics).setFont(Constants.DefaultFont);
        // drawString の Y 座標: y + aMetrics.getAscent() + Constants.Margin.y
        // 今回の例では: 20 + 10 + Constants.Margin.y (Constants.Margin.y は不明なので、実際の値に置き換えるか、同様にモック化するか)
        // 仮に Constants.Margin.y が 5 とすると、Y = 20 + 10 + 5 = 35
        // x 座標: x + Constants.Margin.x (Constants.Margin.x も同様)
        // 仮に Constants.Margin.x が 5 とすると、X = 10 + 5 = 15
        verify(mockGraphics).drawString(
            node.getName(),
            node.getLocation().x + Constants.Margin.x, // x座標
            node.getLocation().y + mockFontMetrics.getAscent() + Constants.Margin.y // y座標
        );
    }

    

    // `stringWidth()` メソッドのテスト
    /**
     * {@code stringWidth()} メソッドが文字列の幅を正しく計算するかをテストします。
     * このメソッドは内部で Graphics オブジェクトを生成するため、Mockito でのモック化は行わず、
     * 実際の実行結果の特性（長い文字列ほど幅が大きくなるなど）を検証します。
     */
    @Test
    public void testStringWidth() {
        // 異なる文字列で幅が適切に変化することを確認します。
        // 同じフォント設定であれば、長い文字列ほど幅が大きくなるはずです。
        assertTrue("短い文字列より長い文字列の方が幅が大きいこと", node.stringWidth("A") < node.stringWidth("AAA"));
        assertTrue("同じ文字列であれば幅は同じであること", node.stringWidth("Test") == node.stringWidth("Test"));
        assertTrue("幅が0より大きいこと", node.stringWidth("SomeText") > 0);
    }

    

    // `stringHeight()` メソッドのテスト
    /**
     * {@code stringHeight()} メソッドが文字列の高さを正しく計算するかをテストします。
     * このメソッドも内部で Graphics オブジェクトを生成するため、Mockito でのモック化は行わず、
     * 実際の実行結果の特性（フォントが同じなら文字列の内容に関わらず高さは同じなど）を検証します。
     */
    @Test
    public void testStringHeight() {
        int height1 = node.stringHeight("SingleLine");
        int height2 = node.stringHeight("Another Line of Text");

        // 同じフォント設定であれば、文字列の内容に関わらず高さは同じになるはずです。
        assertEquals("フォントが同じなら高さは同じであること", height1, height2);
        assertTrue("高さが0より大きいこと", height1 > 0);
    }
}
