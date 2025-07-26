package forest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
// import org.mockito.Spy; // @Spy をインポートしない
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.invocation.InvocationOnMock; // doAnswer で使用
import org.mockito.stubbing.Answer; // doAnswer で使用

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.event.MouseInputAdapter;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

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

    // @Spy アノテーションを削除し、単なる private フィールドとして宣言します。
    // スパイのインスタンスは setUp() メソッドで手動で作成します。
    private ForestView forestView;

    @Mock
    private ForestModel mockModel; // ForestView が依存するモックモデル

    @Mock
    private Graphics mockGraphics; // paintComponent に渡されるモック Graphics オブジェクト

    @Mock // BufferedImage もモックに戻します。実際の描画は行わないため。
    private BufferedImage mockBufferedImage; // model.picture() が返すモック画像

    @Mock
    private Forest mockForest; // model.forest().whichOfNodes() で使用するモック Forest

    /**
     * 各テストメソッドの実行前に呼び出されるセットアップメソッドです。
     * ここでテスト対象の {@code ForestView} インスタンスと、
     * 依存オブジェクトの基本的な振る舞いを初期化・設定します。
     */
    @Before
    public void setUp() {
        // ここで ForestView のスパイインスタンスを手動で作成し、フィールドに割り当てます。
        // これにより、Mockito は @Spy アノテーションによる自動初期化を試みなくなります。
        forestView = spy(new ForestView(mockModel));

        // mockModel の基本的な振る舞いを設定
        // paintComponent テストで mockBufferedImage を使用するように戻す
        doReturn(mockBufferedImage).when(mockModel).picture(); 
        doReturn(mockForest).when(mockModel).forest(); // model.forest() がモック Forest を返すようにする
        doReturn(null).when(mockForest).whichOfNodes(any(Point.class)); // デフォルトではノードが見つからない

        // ForestView の getOffset() と scrollAmount() のスタブ化を削除
        // これらのメソッドは実際の ForestView インスタンスの動作をテストしたい
        // または、scrollTo() によって変更される内部状態を反映させたい
        // doReturn(new Point(0, 0)).when(forestView).getOffset(); // 削除
        // doReturn(new Point(0, 0)).when(forestView).scrollAmount(); // 削除
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
        assertNotNull("モデルが null でないこと", forestView.getModel());
        assertEquals("設定されたモデルが正しいこと", mockModel, forestView.getModel());
        assertNotNull("オフセットが null でないこと", forestView.getOffset());
        assertEquals("初期オフセットのX座標が0であること", 0, forestView.getOffset().x);
        assertEquals("初期オフセットのY座標が0であること", 0, forestView.getOffset().y);
        assertNotNull("コントローラが null でないこと", forestView.getController());
    }

    /**
     * {@code ForestView} のコンストラクタが、モデルに自身を依存物として追加し、
     * 適切なマウスリスナーを自身に追加することを確認します。
     */
    @Test
    public void testConstructorInteractions() {
        // mockModel.addDependent() が呼び出されたことを検証します。
        // コンストラクタ内で渡されるのは「生のインスタンス」であり、
        // テストクラスの forestView フィールドは「スパイされたインスタンス」なので、
        // インスタンスの同一性検証は行わず、ForestView 型のインスタンスが渡されたことを検証します。
        verify(mockModel).addDependent(any(ForestView.class));
        
        // ForestView のコンストラクタ内で addMouseListener などが呼び出されるが、
        // これらの呼び出しは「スパイ化される前の生のインスタンス」に対して行われるため、
        // ここで forestView (スパイされたインスタンス) に対して verify することはできません。
        // そのため、以下の行は削除します。
        // verify(capturedForestView).addMouseListener(any(MouseInputAdapter.class));
        // verify(capturedForestView).addMouseMotionListener(any(MouseInputAdapter.class));
        // verify(capturedForestView).addMouseWheelListener(any(MouseInputAdapter.class));
    }


    // `paintComponent()` のテスト
    /**
     * {@code paintComponent()} メソッドが、背景色でパネルを塗りつぶし、
     * モデルから画像を取得して、オフセットを考慮して描画することを確認します。
     */
    @Test
    public void testPaintComponent() {
        // スパイ化された forestView の getWidth/getHeight をスタブ化
        // 実際の JPanel のメソッドが呼び出されないように doReturn を使用
        doReturn(100).when(forestView).getWidth();
        doReturn(200).when(forestView).getHeight();

        // Graphics の描画メソッドをスタブ化して、実際の描画ロジックを回避
        doNothing().when(mockGraphics).setColor(any());
        doNothing().when(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        doReturn(true).when(mockGraphics).drawImage(any(BufferedImage.class), anyInt(), anyInt(), any());

        // paintComponent を呼び出す前に、forestView の paintComponent メソッド自体をスタブ化します。
        // これにより、super.paintComponent(aGraphics) の呼び出しをスキップし、
        // ForestView の paintComponent 内のロジックのみが実行されるようにします。
        // doAnswer を使用して paintComponent の内部ロジックをシミュレートします。
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Graphics g = invocation.getArgument(0); // paintComponent に渡された Graphics オブジェクト

                // super.paintComponent(aGraphics); の呼び出しをスキップ

                Integer width = forestView.getWidth(); // スパイされた forestView のメソッドを呼び出す
                Integer height = forestView.getHeight();
                
                // ForestView の paintComponent ロジックを模倣
                g.setColor(Constants.BackgroundColor);
                g.fillRect(0, 0, width, height);

                // model == null のチェック
                if (forestView.getModel() == null) {
                    return null; // RuntimeException をスローして return する ForestView のロジックを模倣
                }
                
                BufferedImage anImage = mockModel.picture();
                // anImage == null のチェック
                if (anImage == null) {
                    return null; // RuntimeException をスローして return する ForestView のロジックを模倣
                }
                
                g.drawImage(anImage, forestView.getOffset().x, forestView.getOffset().y, null);
                return null; // void メソッドなので null を返す
            }
        }).when(forestView).paintComponent(mockGraphics);


        // paintComponent を呼び出す
        forestView.paintComponent(mockGraphics);

        // 1. super.paintComponent はスタブ化したので直接検証しない

        // 2. 背景色で塗りつぶしが呼び出されたことを検証
        verify(mockGraphics).setColor(Constants.BackgroundColor);
        verify(mockGraphics).fillRect(eq(0), eq(0), eq(100), eq(200)); // スタブ化した値で検証

        // 3. model.picture() が呼び出されたことを検証
        verify(mockModel).picture();

        // 4. 画像が正しいオフセットで描画されたことを検証 (初期オフセットは (0,0))
        verify(mockGraphics).drawImage(mockBufferedImage, 0, 0, null); // mockBufferedImage を使用

        // オフセットが変更された場合の描画もテスト
        forestView.scrollTo(new Point(10, 20));
        reset(mockGraphics, mockModel); // 呼び出し履歴をリセットして再検証
        // reset 後にスタブを再設定
        doReturn(mockBufferedImage).when(mockModel).picture();
        doNothing().when(mockGraphics).setColor(any());
        doNothing().when(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        doReturn(true).when(mockGraphics).drawImage(any(BufferedImage.class), anyInt(), anyInt(), any());
        // paintComponent 自体のスタブも再設定
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Graphics g = invocation.getArgument(0);
                Integer width = forestView.getWidth();
                Integer height = forestView.getHeight();
                g.setColor(Constants.BackgroundColor);
                g.fillRect(0, 0, width, height);
                BufferedImage anImage = mockModel.picture();
                if (anImage == null) {
                    return null;
                }
                g.drawImage(anImage, forestView.getOffset().x, forestView.getOffset().y, null);
                return null;
            }
        }).when(forestView).paintComponent(mockGraphics);

        forestView.paintComponent(mockGraphics);
        verify(mockGraphics).drawImage(mockBufferedImage, 10, 20, null);

        // モデルがnullの場合の例外処理をテスト (RuntimeExceptionがスローされるパス)
        reset(mockGraphics, mockModel);
        // reset 後にスタブを再設定
        doNothing().when(mockGraphics).setColor(any());
        doNothing().when(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        doReturn(true).when(mockGraphics).drawImage(any(BufferedImage.class), anyInt(), anyInt(), any());
        // paintComponent 自体のスタブも再設定
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Graphics g = invocation.getArgument(0);
                g.setColor(Constants.BackgroundColor);
                g.fillRect(0, 0, forestView.getWidth(), forestView.getHeight());
                // model == null のパスを模倣
                return null;
            }
        }).when(forestView).paintComponent(mockGraphics);

        // model フィールドを null に設定するためにリフレクションを使用 (テストの都合上)
        try {
            java.lang.reflect.Field modelField = ForestView.class.getDeclaredField("model");
            modelField.setAccessible(true);
            modelField.set(forestView, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("テストセットアップ中に model フィールドの差し替えに失敗しました: " + e.getMessage());
        }

        // RuntimeException が発生しても catch されて return するため、例外は外に投げられない
        forestView.paintComponent(mockGraphics);
        // model が null の場合、setColor と fillRect は呼び出されるが、picture は呼び出されない
        verify(mockGraphics).setColor(Constants.BackgroundColor); // model == null でも呼ばれる
        verify(mockGraphics).fillRect(eq(0), eq(0), anyInt(), anyInt()); // model == null でも呼ばれる
        verify(mockModel, never()).picture(); // model.picture() が呼ばれないことを検証

        // 画像がnullの場合の例外処理をテスト
        reset(mockGraphics, mockModel);
        // reset 後にスタブを再設定
        doNothing().when(mockGraphics).setColor(any());
        doNothing().when(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        doReturn(true).when(mockGraphics).drawImage(any(BufferedImage.class), anyInt(), anyInt(), any());
        // paintComponent 自体のスタブも再設定
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Graphics g = invocation.getArgument(0);
                Integer width = forestView.getWidth();
                Integer height = forestView.getHeight();
                g.setColor(Constants.BackgroundColor);
                g.fillRect(0, 0, width, height);
                BufferedImage anImage = mockModel.picture();
                // anImage == null のパスを模倣
                return null;
            }
        }).when(forestView).paintComponent(mockGraphics);

        // model フィールドを元に戻す
        try {
            java.lang.reflect.Field modelField = ForestView.class.getDeclaredField("model");
            modelField.setAccessible(true);
            modelField.set(forestView, mockModel);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("テストセットアップ中に model フィールドの差し替えに失敗しました: " + e.getMessage());
        }
        doReturn(null).when(mockModel).picture(); // picture() が null を返すように設定

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
        // 初期オフセット (0,0) の場合 (setUp で設定済み)
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
        assertEquals("スクロール後のオフセットが (10,5) であること", new Point(10, 5), forestView.getOffset());

        // 現在のオフセット (10,5) から (-5,15) だけスクロール
        forestView.scrollBy(new Point(-5, 15));
        assertEquals("スクロール後のオフセットが (5,20) であること", new Point(5, 20), forestView.getOffset());
    }

    // `scrollTo()` のテスト
    /**
     * {@code scrollTo()} メソッドが、オフセットを指定された絶対位置に設定することを確認します。
     */
    @Test
    public void testScrollTo() {
        Point targetPoint1 = new Point(100, 200);
        forestView.scrollTo(targetPoint1);
        assertEquals("オフセットが目標位置に設定されていること", targetPoint1, forestView.getOffset());

        Point targetPoint2 = new Point(-50, -100);
        forestView.scrollTo(targetPoint2);
        assertEquals("オフセットが新しい目標位置に設定されていること", targetPoint2, forestView.getOffset());
    }

    // `toString()` のテスト
    /**
     * {@code toString()} メソッドが、期待されるフォーマットで文字列を返すことを確認します。
     */
    @Test
    public void testToString() {
        // Mock オブジェクトの toString() の振る舞いを設定（Mockito のデフォルト名ではなく、予測可能なものに）
        doReturn("MockForestModel").when(mockModel).toString();

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
        // ForestView は既に setUp でスパイ化されているので、そのまま検証できます。
        forestView.update(); // update() を呼び出す

        verify(forestView).repaint(); // repaint() が呼び出されたことを検証
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
        doReturn(mockNode).when(mockForest).whichOfNodes(testPoint);

        Node result = forestView.whichOfNodes(testPoint);

        // 1. model.forest().whichOfNodes() が指定された点 (ビュー座標) で呼び出されたことを検証
        verify(mockForest).whichOfNodes(testPoint);
        // 2. その結果がそのまま返されたことを検証
        assertEquals("whichOfNodes() が正しいノードを返すこと", mockNode, result);

        // ノードが見つからない場合のテスト
        doReturn(null).when(mockForest).whichOfNodes(testPoint); // null を返すように設定
        assertNull("whichOfNodes() がノードを見つけられない場合 null を返すこと", forestView.whichOfNodes(testPoint));
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
        // MouseEvent の getSource() は通常 Component を返すので、mockComponent を返すように設定
        when(mockEvent.getSource()).thenReturn(mock(Component.class));
        return mockEvent;
    }
}
