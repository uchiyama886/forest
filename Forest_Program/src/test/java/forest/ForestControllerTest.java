package forest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor; // メソッドの引数をキャプチャするために使用
import org.mockito.Mock;           // モックオブジェクトを作成するためのアノテーション
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.Component; // MouseEvent.getSource() が返す型
import java.awt.Cursor;    // Cursor.getPredefinedCursor() などで使用
import java.awt.Point;     // 座標を表すクラス
import java.awt.event.InputEvent; // 修飾キーのマスクで使用
import java.awt.event.MouseEvent; // マウスイベントのモック化
import java.awt.event.MouseWheelEvent; // マウスホイールイベントのモック化
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * {@code ForestController} クラスの単体テストクラスです。
 * JUnit 4 と Mockito を利用して、{@code ForestController} の各メソッドが期待通りに動作するかを検証します。
 * {@code ForestModel}, {@code ForestView}, {@code MouseEvent} (およびそのサブクラス)
 * といった依存オブジェクトは Mockito でモック化します。
 */
@RunWith(MockitoJUnitRunner.class)
public class ForestControllerTest {

    private ForestController controller; // テスト対象の ForestController インスタンス

    @Mock
    private ForestModel mockModel; // ForestController が操作するモックモデル

    @Mock
    private ForestView mockView; // ForestController が操作するモックビュー

    @Mock
    private Component mockComponent; // マウスイベントのソースとなるモックコンポーネント (通常は View 自身)

    /**
     * 各テストメソッドの実行前に呼び出されるセットアップメソッドです。
     * ここでテスト対象の {@code ForestController} インスタンスと、
     * 依存オブジェクトの基本的な振る舞いを初期化・設定します。
     */
    @Before
    public void setUp() {
        controller = new ForestController();
        controller.setModel(mockModel); // コントローラにモックモデルを設定
        controller.setView(mockView);   // コントローラにモックビューを設定

        // mockView のデフォルトの振る舞いを設定
        when(mockView.scrollAmount()).thenReturn(new Point(0, 0)); // デフォルトのスクロール量
        when(mockView.whichOfNodes(any(Point.class))).thenReturn(null); // デフォルトではノードが見つからない

        // mockModel の getDependents() の振る舞いを設定 (scrollBy のテストで使用)
        when(mockModel.getDependents()).thenReturn(new ArrayList<>()); // デフォルトでは依存ビューなし
    }

    

    // テストメソッド

    // コンストラクタのテスト
    /**
     * {@code ForestController} コンストラクタがフィールドを正しく初期化することを確認します。
     */
    @Test
    public void testConstructor() {
        ForestController newController = new ForestController(); // 新しいインスタンスをテスト
        assertNull("model が null で初期化されること", newController.getModel());
        assertNull("view が null で初期化されること", newController.getView());
        assertNull("previous が null で初期化されること", newController.getPrevious());
        assertNull("current が null で初期化されること", newController.getCurrent());
    }

    

    // `setModel()` のテスト
    /**
     * {@code setModel()} メソッドがモデルを正しく設定することを確認します。
     */
    @Test
    public void testSetModel() {
        ForestModel anotherMockModel = mock(ForestModel.class);
        controller.setModel(anotherMockModel);
        assertEquals("setModel() がモデルを正しく設定すること", anotherMockModel, controller.getModel());
    }

    

    // `setView()` のテスト
    /**
     * {@code setView()} メソッドがビューを正しく設定し、
     * マウスリスナー、マウスモーションリスナー、マウスホイールリスナーをビューに追加することを確認します。
     */
    @Test
    public void testSetView() {
        ForestView anotherMockView = mock(ForestView.class);
        controller.setView(anotherMockView); // 新しいビューを設定

        assertEquals("setView() がビューを正しく設定すること", anotherMockView, controller.getView());
        // 各リスナーがビューに追加されたことを検証
        verify(anotherMockView).addMouseListener(controller);
        verify(anotherMockView).addMouseMotionListener(controller);
        verify(anotherMockView).addMouseWheelListener(controller);
    }

    

    // `mouseClicked()` のテスト
    /**
     * {@code mouseClicked()} メソッドが、クリックされた座標とノード情報を正しく処理し、標準出力に出力することを確認します。
     * ノードが見つかった場合と見つからなかった場合の両方をテストします。
     */
    @Test
    public void testMouseClicked() {
        // 標準出力をキャプチャするための準備
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        // シナリオ1: ノードがクリックされた場合
        Point clickPoint = new Point(50, 50);
        MouseEvent mockEventWithNode = mock(MouseEvent.class);
        when(mockEventWithNode.getPoint()).thenReturn(clickPoint);
        when(mockView.scrollAmount()).thenReturn(new Point(10, 20)); // ビューがスクロールしている場合
        Node mockClickedNode = mock(Node.class);
        when(mockClickedNode.toString()).thenReturn("MockNode[name=Clicked]"); // NodeのtoString()をモック
        // whichOfNodes がモックノードを返すように設定 (モデル座標で 60, 70 の位置)
        when(mockView.whichOfNodes(new Point(clickPoint.x + 10, clickPoint.y + 20))).thenReturn(mockClickedNode);

        controller.mouseClicked(mockEventWithNode);

        String expectedOutputWithNode = "Clicked at Model Coordinates: " + new Point(60, 70) + "\n" +
                                        "Node/Leaf Clicked: MockNode[name=Clicked]\n";
        assertEquals("ノードがクリックされた場合の出力が正しいこと", expectedOutputWithNode, outContent.toString());

        // 出力をクリアして次のシナリオ
        outContent.reset();

        // シナリオ2: ノードがクリックされなかった場合
        MouseEvent mockEventNoNode = mock(MouseEvent.class);
        when(mockEventNoNode.getPoint()).thenReturn(clickPoint);
        when(mockView.scrollAmount()).thenReturn(new Point(10, 20)); // ビューがスクロールしている場合
        // whichOfNodes が null を返すように設定
        when(mockView.whichOfNodes(new Point(clickPoint.x + 10, clickPoint.y + 20))).thenReturn(null);

        controller.mouseClicked(mockEventNoNode);

        String expectedOutputNoNode = "Clicked at Model Coordinates: " + new Point(60, 70) + "\n" +
                                      "No Node/Leaf found at this location.\n";
        assertEquals("ノードが見つからなかった場合の出力が正しいこと", expectedOutputNoNode, outContent.toString());

        // 標準出力を元に戻す
        System.setOut(System.out);
    }

    /**
     * `mouseClicked` メソッドが、`view` が `null` の場合に `NullPointerException` を発生させないことを確認します。
     */
    @Test
    public void testMouseClickedWithNullView() {
        controller = new ForestController(); // view が null の新しいコントローラ
        // 標準出力をキャプチャするための準備
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getPoint()).thenReturn(new Point(10, 10));

        // NullPointerException が発生しないことを確認
        try {
            controller.mouseClicked(mockEvent);
        } catch (NullPointerException e) {
            fail("view が null の場合に NullPointerException が発生しました");
        }

        String expectedOutput = "Clicked at Model Coordinates: " + new Point(10, 10) + "\n";
        assertEquals("view が null の場合、それ以降の処理が行われないこと", expectedOutput, outContent.toString());

        System.setOut(System.out);
    }

    

    // `mouseDragged()` のテスト
    /**
     * {@code mouseDragged()} メソッドが、マウスカーソルを移動カーソルに変更し、
     * ビューのスクロールを依頼し、その後にビューの再描画を依頼することを確認します。
     */
    @Test
    public void testMouseDragged() {
        // mousePressed が先に呼ばれて previous が設定されている必要がある
        controller.mousePressed(createMouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0)); // previous を (0,0) に設定

        // mouseDragged イベントを作成 (例: (0,0) -> (10,10) にドラッグ)
        MouseEvent dragEvent = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 10, 10, 0);
        when(dragEvent.getSource()).thenReturn(mockComponent);

        controller.mouseDragged(dragEvent);

        // 1. カーソルが MOVE_CURSOR に設定されたことを検証
        verify(mockComponent).setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        // 2. view.scrollBy() が正しい差分 (10, 10) で呼び出されたことを検証
        verify(mockView).scrollBy(new Point(10, 10));

        // 3. previous が current (10,10) に更新されたことを検証
        assertEquals("previous が current に更新されていること", new Point(10, 10), controller.getPrevious());
    }

    

    // `mouseEntered()`, `mouseExited()`, `mouseMoved()` のテスト
    /**
     * これらのメソッドが何もしないことを確認します。
     */
    @Test
    public void testEmptyMouseMethods() {
        MouseEvent mockEvent = mock(MouseEvent.class);

        controller.mouseEntered(mockEvent);
        // 何も起こらないことを検証 (例: モックオブジェクトに対する相互作用がない)
        verifyNoInteractions(mockEvent);

        controller.mouseExited(mockEvent);
        verifyNoInteractions(mockEvent);

        controller.mouseMoved(mockEvent);
        verifyNoInteractions(mockEvent);
    }

    

    // `mousePressed()` のテスト
    /**
     * {@code mousePressed()} メソッドが、マウスカーソルを十字カーソルに変更し、
     * {@code current} と {@code previous} の位置をイベントの位置に設定することを確認します。
     */
    @Test
    public void testMousePressed() {
        Point pressPoint = new Point(25, 30);
        MouseEvent pressEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, pressPoint.x, pressPoint.y, 0);
        when(pressEvent.getSource()).thenReturn(mockComponent);

        controller.mousePressed(pressEvent);

        // 1. カーソルが CROSSHAIR_CURSOR に設定されたことを検証
        verify(mockComponent).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        // 2. current と previous が正しい位置に設定されたことを検証
        assertEquals("current が正しい位置に設定されていること", pressPoint, controller.getCurrent());
        assertEquals("previous が current と同じ位置に設定されていること", pressPoint, controller.getPrevious());
    }

    

    // `mouseReleased()` のテスト
    /**
     * {@code mouseReleased()} メソッドが、マウスカーソルをデフォルトに戻し、
     * {@code current} と {@code previous} の位置をイベントの位置に設定することを確認します。
     */
    @Test
    public void testMouseReleased() {
        Point releasePoint = new Point(75, 80);
        MouseEvent releaseEvent = createMouseEvent(MouseEvent.MOUSE_RELEASED, releasePoint.x, releasePoint.y, 0);
        when(releaseEvent.getSource()).thenReturn(mockComponent);

        controller.mouseReleased(releaseEvent);

        // 1. カーソルが DEFAULT_CURSOR に設定されたことを検証
        verify(mockComponent).setCursor(Cursor.getDefaultCursor());

        // 2. current と previous が正しい位置に設定されたことを検証
        assertEquals("current が正しい位置に設定されていること", releasePoint, controller.getCurrent());
        assertEquals("previous が current と同じ位置に設定されていること", releasePoint, controller.getPrevious());
    }

    

    // `mouseWheelMoved()` のテスト
    /**
     * {@code mouseWheelMoved()} メソッドが、マウスホイールの回転量に基づいて
     * ビューのスクロールを依頼し、ビューの再描画を依頼することを確認します。
     * 修飾キーが押下されている場合は水平スクロールに変わることもテストします。
     */
    @Test
    public void testMouseWheelMoved() {
        // シナリオ1: 垂直スクロール (修飾キーなし)
        MouseWheelEvent verticalScrollEvent = new MouseWheelEvent(mockComponent, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                0, 0, 0, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, -3); // wheel rotation = -3 (上にスクロール)

        controller.mouseWheelMoved(verticalScrollEvent);

        // scrollBy が (0, -(-3)) = (0, 3) で呼び出されたことを検証
        verify(mockView).scrollBy(new Point(0, 3));
        verify(mockView).repaint(); // repaint が呼び出されたことも検証

        reset(mockView); // モックの呼び出し履歴をリセット

        // シナリオ2: 水平スクロール (修飾キーあり)
        MouseWheelEvent horizontalScrollEvent = new MouseWheelEvent(mockComponent, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                InputEvent.SHIFT_DOWN_MASK, 0, 0, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, -2); // wheel rotation = -2 (上にスクロール)

        controller.mouseWheelMoved(horizontalScrollEvent);

        // scrollBy が (-(-2), 0) = (2, 0) で呼び出されたことを検証
        verify(mockView).scrollBy(new Point(2, 0));
        verify(mockView).repaint();

        reset(mockView);

        // シナリオ3: wheelRotation が 0 の場合、何もしないことを検証
        MouseWheelEvent zeroRotationEvent = new MouseWheelEvent(mockComponent, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                0, 0, 0, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 0); // wheel rotation = 0

        controller.mouseWheelMoved(zeroRotationEvent);

        verifyNoInteractions(mockView); // mockView に対する操作がないことを検証
    }

    

    // `scrollBy()` のテスト
    /**
     * {@code scrollBy()} メソッドが、指定された量だけビューをスクロールし、
     * シフトキーの状態と他の修飾キーの状態に応じて、他のビューを連動スクロールすることを確認します。
     */
    @Test
    public void testScrollBy() {
        ForestView mockView3 = mock(ForestView.class);
        when(mockModel.getDependents()).thenReturn(Arrays.asList(mockView, mockView3)); // 他のビューとして mockView3 を設定

        // シナリオ1: シフトキーなし (相対スクロール、他のビューは影響しない)
        Point scrollAmount1 = new Point(10, 5);
        MouseEvent mouseEvent1 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, 0); // 修飾キーなし

        controller.scrollBy(scrollAmount1, mouseEvent1);

        verify(mockView).scrollBy(scrollAmount1); // 自身のビューがスクロールする
        verify(mockView).repaint(); // 自身のビューが再描画される
        verifyNoInteractions(mockView3); // 他のビューは影響しない

        reset(mockView, mockView3, mockModel); // モックの呼び出し履歴をリセット
        when(mockModel.getDependents()).thenReturn(Arrays.asList(mockView, mockView3)); // 再設定

        // シナリオ2: シフトキーのみ (他のビューも相対スクロール)
        Point scrollAmount2 = new Point(-20, 10);
        MouseEvent mouseEvent2 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, InputEvent.SHIFT_DOWN_MASK); // シフトキー

        controller.scrollBy(scrollAmount2, mouseEvent2);

        verify(mockView).scrollBy(scrollAmount2); // 自身のビューがスクロールする
        verify(mockView).repaint(); // 自身のビューが再描画される
        verify(mockView3).scrollBy(scrollAmount2); // 他のビューも相対スクロール
        verify(mockView3).repaint(); // 他のビューも再描画される

        reset(mockView, mockView3, mockModel);
        when(mockModel.getDependents()).thenReturn(Arrays.asList(mockView, mockView3));

        // シナリオ3: シフトキー + Alt/Ctrl/Meta (他のビューも絶対スクロール)
        Point scrollAmount3 = new Point(5, -5);
        Point currentScrollAmount = new Point(100, 200); // mockView の現在のスクロール量
        when(mockView.scrollAmount()).thenReturn(currentScrollAmount); // mockView の scrollAmount を設定
        MouseEvent mouseEvent3 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK); // シフト + Alt

        controller.scrollBy(scrollAmount3, mouseEvent3);

        verify(mockView).scrollBy(scrollAmount3); // 自身のビューは相対スクロール
        verify(mockView).repaint();
        // 他のビューは absolute scroll (0 - scrollAmount.x, 0 - scrollAmount.y) に設定される
        verify(mockView3).scrollTo(new Point(-currentScrollAmount.x, -currentScrollAmount.y));
        verify(mockView3).repaint();

        reset(mockView, mockView3, mockModel);
        when(mockModel.getDependents()).thenReturn(Arrays.asList(mockView, mockView3));

        // シナリオ4: シフトキー + Ctrl
        MouseEvent mouseEvent4 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
        controller.scrollBy(scrollAmount3, mouseEvent4);
        verify(mockView3).scrollTo(new Point(-currentScrollAmount.x, -currentScrollAmount.y)); // 他のビューも絶対スクロール

        reset(mockView, mockView3, mockModel);
        when(mockModel.getDependents()).thenReturn(Arrays.asList(mockView, mockView3));

        // シナリオ5: シフトキー + Meta
        MouseEvent mouseEvent5 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, InputEvent.SHIFT_DOWN_MASK | InputEvent.META_DOWN_MASK);
        controller.scrollBy(scrollAmount3, mouseEvent5);
        verify(mockView3).scrollTo(new Point(-currentScrollAmount.x, -currentScrollAmount.y)); // 他のビューも絶対スクロール
    }


    

    // `toString()` のテスト
    /**
     * {@code toString()} メソッドが、期待されるフォーマットで文字列を返すことを確認します。
     */
    @Test
    public void testToString() {
        // Mock オブジェクトの toString() の振る舞いを設定（デフォルトでは Mockito の生成する文字列）
        // 例: mockModel.toString() は "ForestModel$$EnhancerByMockitoWithCGLIB$$..." のようになる
        // そこで、より予測可能な文字列を返すようにスタブ化する。
        when(mockModel.toString()).thenReturn("MockForestModel");
        when(mockView.toString()).thenReturn("MockForestView");

        String expectedString = "forest.ForestController[model=MockForestModel,view=MockForestView]";
        assertEquals("toString() メソッドが期待される文字列を返すこと", expectedString, controller.toString());
    }

    

    // ヘルパーメソッド

    /**
     * MouseEvent オブジェクトをモック化するためのヘルパーメソッド。
     * @param id イベントID
     * @param x イベントのX座標
     * @param y イベントのY座標
     * @param modifiersEx 修飾キー
     * @return モック化された MouseEvent オブジェクト
     */
    private MouseEvent createMouseEvent(int id, int x, int y, int modifiersEx) {
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getID()).thenReturn(id);
        when(mockEvent.getPoint()).thenReturn(new Point(x, y));
        when(mockEvent.getX()).thenReturn(x);
        when(mockEvent.getY()).thenReturn(y);
        when(mockEvent.getModifiersEx()).thenReturn(modifiersEx);
        when(mockEvent.getSource()).thenReturn(mockComponent); // デフォルトのソースを設定
        return mockEvent;
    }
}
