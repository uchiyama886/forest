package forest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor; // メソッドの引数をキャプチャするために使用
import org.mockito.InjectMocks;     // モックを自動的にインジェクトするために使用
import org.mockito.Mock;           // モックオブジェクトを作成するためのアノテーション
import org.mockito.Spy;             // 実際の一部メソッドを呼び出すために使用
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.Graphics;       // paintComponent の引数
import java.awt.Point;          // 座標
import java.awt.Rectangle;      // bounds や領域
import java.awt.image.BufferedImage; // picture() の戻り値
import javax.swing.event.MouseInputAdapter; // Controller の継承元

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * {@code ForestView} クラスの単体テストクラスです。
 * JUnit 4 と Mockito を利用して、{@code ForestView} の各メソッドが期待通りに動作するかを検証します。
 * {@code ForestModel}, {@code Graphics}, {@code BufferedImage}, {@code Node}
 * といった依存オブジェクトは Mockito でモック化します。
 * <p>
 * 注: {@code paintComponent} のテストでは、{@code Graphics} オブジェクトの描画メソッドが適切に呼び出されることを検証します。
 * GUI コンポーネントの見た目そのもののテストは、より高レベルなテストツール (例: FEST-Swing) が適していますが、
 * ここではロジックの呼び出しに焦点を当てます。
 * また、{@code ForestController} のインスタンス化は View 内部で行われるため、その相互作用の検証には工夫が必要です。
 */
@RunWith(MockitoJUnitRunner.class)
public class ForestViewTest {

    // @InjectMocks を使用すると、コンストラクタインジェクションが自動的に行われます。
    // ForestView のコンストラクタは ForestModel を引数に取るため、mockModel が自動的に注入されます。
    @InjectMocks
    private ForestView forestView;

    @Mock
    private ForestModel mockModel; // ForestView が依存するモックモデル

    @Mock
    private Graphics mockGraphics; // paintComponent に渡されるモック Graphics オブジェクト

    @Mock
    private BufferedImage mockBufferedImage; // model.picture() が返すモック画像

    @Mock
    private Forest mockForest; // model.forest().whichOfNodes() で使用するモック Forest

    // Constants クラスは static final なので、テストで直接モック化することはできません。
    // テストによっては、Constants の値を一時的に変更するためのリフレクションを使うこともありますが、
    // ここではその必要はありません。

    /**
     * 各テストメソッドの実行前に呼び出されるセットアップメソッドです。
     * ここでテスト対象の {@code ForestView} インスタンスと、
     * 依存オブジェクトの基本的な振る舞いを初期化・設定します。
     */
    @Before
    public void setUp() {
        // mockModel の基本的な振る舞いを設定
        when(mockModel.picture()).thenReturn(mockBufferedImage); // model.picture() がモック画像を返すようにする
        when(mockModel.forest()).thenReturn(mockForest); // model.forest() がモック Forest を返すようにする
        when(mockForest.whichOfNodes(any(Point.class))).thenReturn(null); // デフォルトではノードが見つからない

        // ForestView のコンストラクタ内で addDependent が呼び出されることを確認するために、
        // mockModel に addDependent が呼ばれることを検証できます。
        // @InjectMocks がコンストラクタインジェクションを行うため、
        // ForestView のインスタンス化と同時に mockModel.addDependent(this) が呼ばれます。
        verify(mockModel).addDependent(forestView);

        // ForestView のコンストラクタ内で ForestController が new され、
        // setModel() と setView() が呼び出されるため、その呼び出しも検証できます。
        // ForestView を Spy 化するか、ForestController を引数で受け取るようにするなどの工夫が必要ですが、
        // ここでは ForestController がリスナーとして View に追加されたことを検証します。
        // ForestView は JPanel の addMouseListener などを呼び出すので、それらを検証できます。
        verify(forestView).addMouseListener(any(MouseInputAdapter.class)); // ForestController は MouseInputAdapter を継承
        verify(forestView).addMouseMotionListener(any(MouseInputAdapter.class));
        verify(forestView).addMouseWheelListener(any(MouseInputAdapter.class));
    }

    

    // テストメソッド

    // コンストラクタのテスト
    /**
     * {@code ForestView} コンストラクタがモデルを設定し、自身をモデルの依存物として登録し、
     * コントローラを初期化してモデルとビューを設定することを確認します。
     */
    @Test
    public void testConstructorInitialization() {
        // setUp() で既に検証済みのため、ここでは追加の検証は不要ですが、
        // model と offset が正しく設定されていることを確認します。
        assertNotNull("モデルが null でないこと", forestView.model);
        assertEquals("設定されたモデルが正しいこと", mockModel, forestView.model);
        assertNotNull("オフセットが null でないこと", forestView.offset);
        assertEquals("初期オフセットのX座標が0であること", 0, forestView.offset.x);
        assertEquals("初期オフセットのY座標が0であること", 0, forestView.offset.y);
        assertNotNull("コントローラが null でないこと", forestView.controller);
    }

    

    // `paintComponent()` のテスト
    /**
     * {@code paintComponent()} メソッドが、背景色でパネルを塗りつぶし、
     * モデルから画像を取得して、オフセットを考慮して描画することを確認します。
     */
    @Test
    public void testPaintComponent() {
        // View のサイズを設定 (JPanel の getWidth/getHeight をモック化できないため、
        // 実際の JPanel の振る舞いに依存するか、テスト用にメソッドをオーバーライドするなどが必要。
        // ここでは、デフォルトでサイズが取得できると仮定し、 Graphics.fillRect の引数を検証します。)
        // ForestView をスパイ化して getWidth/getHeight をスタブ化することも可能ですが、
        // 基本的な Graphics 呼び出しの検証に焦点を当てます。

        // paintComponent を呼び出す
        forestView.paintComponent(mockGraphics);

        // 1. super.paintComponent が呼び出されたことを検証 (JPanel のメソッドなので直接は検証できないが、
        // 一般的には最初の呼び出しとして期待される)

        // 2. 背景色で塗りつぶしが呼び出されたことを検証
        verify(mockGraphics).setColor(Constants.BackgroundColor);
        // getWidth() と getHeight() の具体的な値を検証するために、forestView をスパイ化してスタブ化する必要があります。
        // 例えば、以下のように。
        // ForestView spyView = spy(forestView);
        // doReturn(100).when(spyView).getWidth();
        // doReturn(200).when(spyView).getHeight();
        // spyView.paintComponent(mockGraphics);
        // verify(mockGraphics).fillRect(0, 0, 100, 200);
        // 現状は引数の具体的な値は検証せず、呼び出しのみ検証。
        verify(mockGraphics).fillRect(eq(0), eq(0), anyInt(), anyInt());

        // 3. model.picture() が呼び出されたことを検証
        verify(mockModel).picture();

        // 4. 画像が正しいオフセットで描画されたことを検証 (初期オフセットは (0,0))
        verify(mockGraphics).drawImage(mockBufferedImage, 0, 0, null);

        // オフセットが変更された場合の描画もテスト
        forestView.scrollTo(new Point(10, 20));
        reset(mockGraphics); // 呼び出し履歴をリセットして再検証

        forestView.paintComponent(mockGraphics);
        verify(mockGraphics).drawImage(mockBufferedImage, 10, 20, null);

        // モデルがnullの場合の例外処理をテスト (RuntimeExceptionがスローされるパス)
        reset(mockGraphics, mockModel);
        when(mockModel.picture()).thenReturn(mockBufferedImage); // 必要に応じて再度スタブ化

        // リフレクションを使って model フィールドを null に設定
        try {
            java.lang.reflect.Field modelField = ForestView.class.getDeclaredField("model");
            modelField.setAccessible(true);
            modelField.set(forestView, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("テストセットアップ中に model フィールドの差し替えに失敗しました: " + e.getMessage());
        }

        // RuntimeException が発生しても catch されて return するため、例外は外に投げられない
        // ただし、mockito の verify が呼び出されないことでそのパスを通ったことを確認できる
        forestView.paintComponent(mockGraphics);
        verify(mockGraphics, never()).setColor(any()); // 何も描画されないことを検証
        verify(mockModel, never()).picture(); // model.picture() が呼ばれないことを検証

        // 画像がnullの場合の例外処理をテスト
        reset(mockGraphics, mockModel);
        // model フィールドを元に戻す
        try {
            java.lang.reflect.Field modelField = ForestView.class.getDeclaredField("model");
            modelField.setAccessible(true);
            modelField.set(forestView, mockModel);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("テストセットアップ中に model フィールドの差し替えに失敗しました: " + e.getMessage());
        }
        when(mockModel.picture()).thenReturn(null); // picture() が null を返すように設定

        forestView.paintComponent(mockGraphics);
        // model.picture() は呼ばれるが、drawImage は呼ばれないことを検証
        verify(mockModel).picture();
        verify(mockGraphics, never()).drawImage(any(), anyInt(), anyInt(), any());
    }

    

    // `scrollAmount()` のテスト
    /**
     * {@code scrollAmount()} メソッドが、現在のオフセットの逆向きの値を正しく返すことを確認します。
     */
    @Test
    public void testScrollAmount() {
        // 初期オフセット (0,0) の場合
        assertEquals("初期スクロール量は (0,0) であること", new Point(0, 0), forestView.scrollAmount());

        // オフセットが (10, 20) の場合
        forestView.scrollTo(new Point(10, 20));
        assertEquals("オフセット (10,20) の場合、スクロール量は (-10,-20) であること", new Point(-10, -20), forestView.scrollAmount());

        // オフセットが (-5, -15) の場合
        forestView.scrollTo(new Point(-5, -15));
        assertEquals("オフセット (-5,-15) の場合、スクロール量は (5,15) であること", new Point(5, 15), forestView.scrollAmount());
    }

    

    // `scrollBy()` のテスト
    /**
     * {@code scrollBy()} メソッドが、現在のオフセットに指定された差分を加えて、
     * オフセットを相対的に変更することを確認します。
     */
    @Test
    public void testScrollBy() {
        // 初期オフセット (0,0) から (10,5) だけスクロール
        forestView.scrollBy(new Point(10, 5));
        assertEquals("スクロール後のオフセットが (10,5) であること", new Point(10, 5), forestView.offset);

        // 現在のオフセット (10,5) から (-5,15) だけスクロール
        forestView.scrollBy(new Point(-5, 15));
        assertEquals("スクロール後のオフセットが (5,20) であること", new Point(5, 20), forestView.offset);
    }

    

    // `scrollTo()` のテスト
    /**
     * {@code scrollTo()} メソッドが、オフセットを指定された絶対位置に設定することを確認します。
     */
    @Test
    public void testScrollTo() {
        Point targetPoint1 = new Point(100, 200);
        forestView.scrollTo(targetPoint1);
        assertEquals("オフセットが目標位置に設定されていること", targetPoint1, forestView.offset);

        Point targetPoint2 = new Point(-50, -100);
        forestView.scrollTo(targetPoint2);
        assertEquals("オフセットが新しい目標位置に設定されていること", targetPoint2, forestView.offset);
    }

    

    // `toString()` のテスト
    /**
     * {@code toString()} メソッドが、期待されるフォーマットで文字列を返すことを確認します。
     */
    @Test
    public void testToString() {
        // Mock オブジェクトの toString() の振る舞いを設定（Mockito のデフォルト名ではなく、予測可能なものに）
        when(mockModel.toString()).thenReturn("MockForestModel");

        String expectedString = "forest.ForestView[model=MockForestModel,offset=java.awt.Point[x=0,y=0]]";
        assertEquals("toString() メソッドが期待される文字列を返すこと", expectedString, forestView.toString());

        // オフセットが変更された場合の toString() をテスト
        forestView.scrollTo(new Point(10, 20));
        expectedString = "forest.ForestView[model=MockForestModel,offset=java.awt.Point[x=10,y=20]]";
        assertEquals("オフセット変更後の toString() が正しいこと", expectedString, forestView.toString());
    }

    

    // `update()` のテスト
    /**
     * {@code update()} メソッドが、ビューの {@code repaint()} メソッドを呼び出すことを確認します。
     */
    @Test
    public void testUpdate() {
        // ForestView をスパイ化して repaint() の呼び出しを検証できるようにする
        ForestView spyForestView = spy(forestView);

        spyForestView.update(); // update() を呼び出す

        verify(spyForestView).repaint(); // repaint() が呼び出されたことを検証
    }

    

    // `whichOfNodes()` のテスト
    /**
     * {@code whichOfNodes()} メソッドが、モデルの {@code Forest} オブジェクトの {@code whichOfNodes()} を呼び出し、
     * その結果をそのまま返すことを確認します。
     */
    @Test
    public void testWhichOfNodes() {
        Point testPoint = new Point(50, 50);
        Node mockNode = mock(Node.class);

        // mockForest.whichOfNodes() が特定のノードを返すように設定
        when(mockForest.whichOfNodes(testPoint)).thenReturn(mockNode);

        Node result = forestView.whichOfNodes(testPoint);

        // 1. model.forest().whichOfNodes() が指定された点 (ビュー座標) で呼び出されたことを検証
        verify(mockForest).whichOfNodes(testPoint);
        // 2. その結果がそのまま返されたことを検証
        assertEquals("whichOfNodes() が正しいノードを返すこと", mockNode, result);

        // ノードが見つからない場合のテスト
        when(mockForest.whichOfNodes(testPoint)).thenReturn(null); // null を返すように設定
        assertNull("whichOfNodes() がノードを見つけられない場合 null を返すこと", forestView.whichOfNodes(testPoint));
    }
}
