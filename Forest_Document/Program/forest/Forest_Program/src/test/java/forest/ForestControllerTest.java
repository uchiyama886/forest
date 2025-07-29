package forest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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

    private ForestController controller;

    @Mock
    private ForestModel mockModel;

    @Mock
    private ForestView mockView;

    @Mock
    private Component mockComponent;

    /**
     * 各テストメソッドの実行前に呼び出されるセットアップメソッドです。
     * ここでテスト対象の {@code ForestController} インスタンスと、
     * 依存オブジェクトの基本的な振る舞いを初期化・設定します。
     */
    @Before
    public void setUp() {
        controller = new ForestController();
        controller.setModel(mockModel);
        controller.setView(mockView);

        // mockView のデフォルトの振る舞いを設定
        when(mockView.scrollAmount()).thenReturn(new Point(0, 0)); // デフォルトのスクロール量
        when(mockView.whichOfNodes(any(Point.class))).thenReturn(null); // デフォルトではノードが見つからない

        // mockModel の getDependents() の振る舞いを設定 (scrollBy のテストで使用)
        when(mockModel.getDependents()).thenReturn(new ArrayList<>()); // デフォルトでは依存ビューなし
    }

    // テストメソッド

    /**
     * {@code ForestController} コンストラクタがフィールドを正しく初期化することを確認します。
     */
    @Test
    public void testConstructor() {
        ForestController newController = new ForestController();
        assertNull("model が null で初期化されること", newController.getModel());
        assertNull("view が null で初期化されること", newController.getView());
        assertNull("previous が null で初期化されること", newController.getPrevious());
        assertNull("current が null で初期化されること", newController.getCurrent());
    }

    /**
     * {@code setModel()} メソッドがモデルを正しく設定することを確認します。
     */
    @Test
    public void testSetModel() {
        ForestModel anotherMockModel = mock(ForestModel.class);
        controller.setModel(anotherMockModel);
        assertEquals("setModel() がモデルを正しく設定すること", anotherMockModel, controller.getModel());
    }

    /**
     * {@code setView()} メソッドがビューを正しく設定し、
     * マウスリスナー、マウスモーションリスナー、マウスホイールリスナーをビューに追加することを確認します。
     */
    @Test
    public void testSetView() {
        ForestView anotherMockView = mock(ForestView.class);
        controller.setView(anotherMockView);

        assertEquals("setView() がビューを正しく設定すること", anotherMockView, controller.getView());
        verify(anotherMockView).addMouseListener(controller);
        verify(anotherMockView).addMouseMotionListener(controller);
        verify(anotherMockView).addMouseWheelListener(controller);
    }

    /**
     * {@code mouseClicked()} メソッドが、クリックされた座標とノード情報を正しく処理し、標準出力に出力することを確認します。
     * ノードが見つかった場合と見つからなかった場合の両方をテストします。
     */
    @Test
    public void testMouseClicked() {
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        // シナリオ1: ノードがクリックされた場合
        Point clickPoint = new Point(50, 50);
        MouseEvent mockEventWithNode = mock(MouseEvent.class);
        when(mockEventWithNode.getPoint()).thenReturn(clickPoint);
        when(mockView.scrollAmount()).thenReturn(new Point(10, 20));
        Node mockClickedNode = mock(Node.class);
        when(mockClickedNode.toString()).thenReturn("MockNode[name=Clicked]");
        when(mockView.whichOfNodes(new Point(clickPoint.x + 10, clickPoint.y + 20))).thenReturn(mockClickedNode);

        controller.mouseClicked(mockEventWithNode);

        String expectedOutputWithNode = "Clicked at Model Coordinates: " + new Point(60, 70) + "\n" +
                                        "Node/Leaf Clicked: MockNode[name=Clicked]\n";
        assertEquals("ノードがクリックされた場合の出力が正しいこと", expectedOutputWithNode, outContent.toString());

        outContent.reset();

        // シナリオ2: ノードがクリックされなかった場合
        MouseEvent mockEventNoNode = mock(MouseEvent.class);
        when(mockEventNoNode.getPoint()).thenReturn(clickPoint);
        when(mockView.scrollAmount()).thenReturn(new Point(10, 20));
        when(mockView.whichOfNodes(new Point(clickPoint.x + 10, clickPoint.y + 20))).thenReturn(null);

        controller.mouseClicked(mockEventNoNode);

        String expectedOutputNoNode = "Clicked at Model Coordinates: " + new Point(60, 70) + "\n" +
                                      "No Node/Leaf found at this location.\n";
        assertEquals("ノードが見つからなかった場合の出力が正しいこと", expectedOutputNoNode, outContent.toString());

        System.setOut(System.out);
    }

    /**
     * {@code mouseDragged()} メソッドが、マウスカーソルを移動カーソルに変更し、
     * ビューのスクロールを依頼し、その後にビューの再描画を依頼することを確認します。
     */
    @Test
    public void testMouseDragged() {
        controller.mousePressed(createMouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0));

        MouseEvent dragEvent = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 10, 10, 0);
        when(dragEvent.getSource()).thenReturn(mockComponent);

        controller.mouseDragged(dragEvent);

        verify(mockComponent).setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        verify(mockView).scrollBy(new Point(10, 10));
        assertEquals("previous が current に更新されていること", new Point(10, 10), controller.getPrevious());
    }

    /**
     * これらのメソッドが何もしないことを確認します。
     */
    @Test
    public void testEmptyMouseMethods() {
        MouseEvent mockEvent = mock(MouseEvent.class);

        controller.mouseEntered(mockEvent);
        verifyNoInteractions(mockEvent);

        controller.mouseExited(mockEvent);
        verifyNoInteractions(mockEvent);

        controller.mouseMoved(mockEvent);
        verifyNoInteractions(mockEvent);
    }

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

        verify(mockComponent).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        assertEquals("current が正しい位置に設定されていること", pressPoint, controller.getCurrent());
        assertEquals("previous が current と同じ位置に設定されていること", pressPoint, controller.getPrevious());
    }

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

        verify(mockComponent).setCursor(Cursor.getDefaultCursor());
        assertEquals("current が正しい位置に設定されていること", releasePoint, controller.getCurrent());
        assertEquals("previous が current と同じ位置に設定されていること", releasePoint, controller.getPrevious());
    }

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
        verify(mockView).repaint();

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

    // `scrollBy()` のテスト (リファクタリング版)

    /**
     * {@code scrollBy()} メソッドが、シフトキーなしの場合に、
     * 自身のビューのみを相対スクロールすることを確認します。
     */
    @Test
    public void testScrollBy_NoShiftKey() {
        // setUp() で mockModel.getDependents() は空リストを返すように設定されている
        // mockView.scrollAmount() も new Point(0,0) を返すように設定されている

        Point scrollAmount1 = new Point(10, 5);
        MouseEvent mouseEvent1 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, 0); // 修飾キーなし

        controller.scrollBy(scrollAmount1, mouseEvent1);

        verify(mockView).scrollBy(scrollAmount1); // 自身のビューがスクロールする
        verify(mockView).repaint(); // 自身のビューが再描画される
        verify(mockModel, never()).getDependents(); // シフトキーがないので getDependents() は呼び出されないはず
    }

    /**
     * {@code scrollBy()} メソッドが、シフトキーのみの場合に、
     * 自身のビューと依存ビューを相対スクロールすることを確認します。
     */
    @Test
    public void testScrollBy_ShiftKeyOnly() {
        ForestView mockView3 = mock(ForestView.class);
        when(mockModel.getDependents()).thenReturn(Arrays.asList(mockView, mockView3)); // 他のビューとして mockView3 を設定

        Point scrollAmount2 = new Point(-20, 10);
        MouseEvent mouseEvent2 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, InputEvent.SHIFT_DOWN_MASK); // シフトキー

        controller.scrollBy(scrollAmount2, mouseEvent2);

        verify(mockView).scrollBy(scrollAmount2); // 自身のビューがスクロールする
        verify(mockView).repaint(); // 自身のビューが再描画される
        verify(mockView3).scrollBy(scrollAmount2); // 他のビューも相対スクロール
        verify(mockView3).repaint(); // 他のビューも再描画される
    }

    /**
     * {@code scrollBy()} メソッドが、シフトキーと他の修飾キー (Alt/Ctrl/Meta) が押下されている場合に、
     * 自身のビューは相対スクロールし、他のビューは絶対スクロールすることを確認します。
     */
    @Test
    public void testScrollBy_ShiftAltCtrlMetaKeys() {
        ForestView mockView3 = mock(ForestView.class);
        when(mockModel.getDependents()).thenReturn(Arrays.asList(mockView, mockView3)); // 他のビューとして mockView3 を設定

        Point scrollAmount3 = new Point(5, -5);
        Point currentScrollAmount = new Point(100, 200); // mockView の現在のスクロール量
        when(mockView.scrollAmount()).thenReturn(currentScrollAmount); // mockView の scrollAmount を設定

        // シナリオ3: シフトキー + Alt
        MouseEvent mouseEvent3 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK); // シフト + Alt
        System.out.println("DEBUG: mockView.scrollAmount() before controller.scrollBy for Scenario 3: " + mockView.scrollAmount()); // デバッグ出力
        controller.scrollBy(scrollAmount3, mouseEvent3);
        verify(mockView, times(1)).scrollBy(scrollAmount3); // 1回目の呼び出しを検証
        verify(mockView, times(1)).repaint();
        verify(mockView3, times(1)).scrollTo(new Point(-currentScrollAmount.x, -currentScrollAmount.y)); // 他のビューは絶対スクロール
        verify(mockView3, times(1)).repaint();

        // シナリオ4: シフトキー + Ctrl (同じスタブと検証)
        // reset はしない。新しいイベントで再検証。
        MouseEvent mouseEvent4 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
        System.out.println("DEBUG: mockView.scrollAmount() before controller.scrollBy for Scenario 4: " + mockView.scrollAmount()); // デバッグ出力
        controller.scrollBy(scrollAmount3, mouseEvent4);
        verify(mockView, times(2)).scrollBy(scrollAmount3); // 2回目の呼び出しを検証 (総呼び出し回数)
        verify(mockView, times(2)).repaint();
        verify(mockView3, times(2)).scrollTo(new Point(-currentScrollAmount.x, -currentScrollAmount.y)); // 2回目の呼び出し (総呼び出し回数)
        verify(mockView3, times(2)).repaint();

        // シナリオ5: シフトキー + Meta (同じスタブと検証)
        MouseEvent mouseEvent5 = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 0, InputEvent.SHIFT_DOWN_MASK | InputEvent.META_DOWN_MASK);
        System.out.println("DEBUG: mockView.scrollAmount() before controller.scrollBy for Scenario 5: " + mockView.scrollAmount()); // デバッグ出力
        controller.scrollBy(scrollAmount3, mouseEvent5);
        verify(mockView, times(3)).scrollBy(scrollAmount3); // 3回目の呼び出しを検証 (総呼び出し回数)
        verify(mockView, times(3)).repaint();
        verify(mockView3, times(3)).scrollTo(new Point(-currentScrollAmount.x, -currentScrollAmount.y)); // 3回目の呼び出し (総呼び出し回数)
        verify(mockView3, times(3)).repaint();
    }


    /**
     * {@code toString()} メソッドが、期待されるフォーマットで文字列を返すことを確認します。
     */
    @Test
    public void testToString() {
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
        when(mockEvent.getSource()).thenReturn(mockComponent);
        return mockEvent;
    }
}
