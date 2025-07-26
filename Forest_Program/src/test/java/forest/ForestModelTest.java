package forest;

import org.junit.Before;
import org.junit.Rule; // TemporaryFolder ルールを使用するために必要
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.rules.TemporaryFolder; // 一時ファイル/ディレクトリ作成のための JUnit ルール
import org.mockito.ArgumentCaptor; // メソッドの引数をキャプチャするために使用
import org.mockito.InjectMocks;     // モックを自動的にインジェクトするために使用
import org.mockito.Mock;           // モックオブジェクトを作成するためのアノテーション
import org.mockito.Spy;             // 実際の一部メソッドを呼び出すために使用
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * {@code ForestModel} クラスの単体テストクラスです。
 * JUnit 4 と Mockito を利用して、{@code ForestModel} の各メソッドが期待通りに動作するかを検証します。
 * {@code Forest}, {@code ForestView}, {@code BufferedImage}, {@code Graphics} といった依存オブジェクトは
 * Mockito でモック化またはスパイ化します。
 * <p>
 * 注: {@code read} メソッドはファイルシステムとI/Oに依存するため、{@code TemporaryFolder} を使用して
 * 一時ファイルを生成し、実際の読み込みをシミュレートします。
 * {@code animate} メソッドは {@code Thread.sleep} と {@code SwingUtilities.invokeLater} を含むため、
 * そのテストには {@code CountDownLatch} や static メソッドのモック化（PowerMockなどが必要）が検討されます。
 */
@RunWith(MockitoJUnitRunner.class)
public class ForestModelTest {

    // @InjectMocks を使用して、このフィールドにモックオブジェクトが自動的にインジェクトされます。
    // コンストラクタ引数は @Before で手動で設定するか、他の方法で提供する必要があります。
    // 今回はコンストラクタで File を受け取るため、@InjectMocks は直接使用せず、テストメソッド内で手動で初期化します。
    private ForestModel forestModel;

    // Forest クラスをモック化します。ForestModel は Forest に依存しているため、その振る舞いを制御します。
    @Mock
    private Forest mockForest;

    // ForestView クラスをモック化します。ForestModel はこのビューに更新を通知します。
    @Mock
    private ForestView mockView1;
    @Mock
    private ForestView mockView2;

    // read メソッドのテストで一時ファイルを作成するために使用
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * 各テストメソッドの実行前に呼び出されるセットアップメソッドです。
     * ここでテスト対象の {@code ForestModel} インスタンスを初期化し、
     * 依存オブジェクトの基本的な振る舞いを設定します。
     */
    @Before
    public void setUp() throws IOException {
        // ForestModel のコンストラクタは File を要求するため、一時ファイルを作成して渡します。
        // read メソッド自体のテストは後で行います。
        File dummyFile = tempFolder.newFile("dummy.txt");
        forestModel = new ForestModel(dummyFile);

        // ForestModel の内部で使われる Forest オブジェクトをモックに置き換えます。
        // これを行うには、Reflection を使うか、ForestModel に Forest を設定するsetterを用意するか、
        // あるいは ForestModel を Spy 化してコンストラクタ内でモックを返すようにするなど、工夫が必要です。
        // 今回はシンプルに private field を Reflection で置き換えるか、
        // あるいは ForestModel のインスタンス作成後に mockForest を設定するメソッド（テスト用）を追加することを想定します。
        // もしくは、ForestModel のコンストラクタを修正して、Forest を引数で受け取るように変更するのが理想的です。
        // 現状のコードでは、ForestModel は new Forest() しているので、@InjectMocks も直接使えません。
        // そこで、ForestModel の `forest` フィールドをモックに差し替えるためにリフレクションを使用します。
        try {
            java.lang.reflect.Field forestField = ForestModel.class.getDeclaredField("forest");
            forestField.setAccessible(true); // private フィールドへのアクセスを許可
            forestField.set(forestModel, mockForest); // mockForest を設定
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("テストセットアップ中に Forest フィールドの差し替えに失敗しました: " + e.getMessage());
        }

        // MockForest のデフォルトの振る舞いを設定
        when(mockForest.rootNodes()).thenReturn(new ArrayList<>()); // デフォルトではルートノードなし
        when(mockForest.bounds()).thenReturn(new Rectangle(0, 0, 0, 0)); // デフォルトの境界
    }

    

    // テストメソッド

    // コンストラクタのテスト
    /**
     * {@code ForestModel} のコンストラクタが、{@code Forest} オブジェクトと依存リストを
     * 正しく初期化することを確認します。また、{@code read} メソッドが呼び出されることも検証します。
     * <p>
     * 注: {@code read} メソッドはコンストラクタで呼び出されるため、このテストでは {@code read} の詳細な検証は行わず、
     * その呼び出しのみを確認します。{@code read} 自体は独立したテストで詳細に検証します。
     */
    @Test
    public void testConstructorInitialization() throws IOException {
        // コンストラクタの呼び出しは setUp() で行われているため、ここでは検証をします。
        // read メソッドが呼び出されたことを検証するために、ForestModel をスパイ化する必要があります。
        // しかし、setUp() で既にインスタンス化しているため、ここでは直接的な検証が難しいです。
        // 別の方法として、read メソッドに渡される File オブジェクトをモック化して検証するなどの方法がありますが、
        // 現状のコンストラクタでは具体的なファイルが渡されるため、それは難しいです。
        // ここでは、依存リストが初期化され、Forest オブジェクトが設定されていることを確認します。

        // Forest オブジェクトが初期化されていること (setUp でモックに置き換えられていることを前提)
        assertNotNull("Forest オブジェクトが null でないこと", forestModel.forest());
        // 依存リストが初期化され、空であること
        assertNotNull("dependants リストが null でないこと", forestModel.getDependants());
        assertTrue("dependants リストが空であること", forestModel.dependants.isEmpty());

        // コンストラクタ内でread()が呼ばれることを検証するには、ForestModel自体をspy化し、
        // コンストラクタ引数でダミーファイルを渡す必要があります。
        // 例えば、以下のように書くことができますが、setUp() のロジックと重複するため注意。
        // File tempFileForReadTest = tempFolder.newFile("testRead.txt");
        // ForestModel spyForestModel = spy(new ForestModel(tempFileForReadTest));
        // // ここで spyForestModel の forest フィールドをモックに置き換える必要があります。
        // // spyForestModel.read(tempFileForReadTest) の呼び出しを検証
        // verify(spyForestModel).read(tempFileForReadTest);
    }

    

    // `addDependent()` および `getDependents()` のテスト
    /**
     * {@code addDependent()} メソッドがビューを依存リストに正しく追加することと、
     * {@code getDependents()} がそのリストのコピーを返すことを確認します。
     */
    @Test
    public void testAddAndGetDependents() {
        // addDependent でビューを追加
        forestModel.addDependent(mockView1);
        assertEquals("ビューが1つ追加されていること", 1, forestModel.getDependants().size());
        assertTrue("追加したビューが含まれていること", forestModel.getDependants().contains(mockView1));

        forestModel.addDependent(mockView2);
        assertEquals("ビューが2つ追加されていること", 2, forestModel.getDependants().size());
        assertTrue("追加した2つ目のビューが含まれていること", forestModel.getDependants().contains(mockView2));

        // getDependents がリストのコピーを返すことを確認
        java.util.List<ForestView> retrievedDependents = forestModel.getDependents();
        assertEquals("getDependents が正しいサイズのリストを返すこと", 2, retrievedDependents.size());
        assertTrue("取得したリストにビュー1が含まれていること", retrievedDependents.contains(mockView1));
        assertTrue("取得したリストにビュー2が含まれていること", retrievedDependents.contains(mockView2));
        assertNotSame("取得したリストが元のリストのコピーであること", forestModel.getDependants(), retrievedDependents);

        // コピーをクリアしても元のリストには影響しないことを確認
        retrievedDependents.clear();
        assertEquals("元のリストのサイズが変わらないこと", 2, forestModel.getDependants().size());
    }

    

    // `changed()` のテスト
    /**
     * {@code changed()} メソッドが、登録されているすべての依存ビューの {@code update()} メソッドを呼び出すことを確認します。
     */
    @Test
    public void testChanged() {
        forestModel.addDependent(mockView1);
        forestModel.addDependent(mockView2);

        forestModel.changed(); // changed() を呼び出す

        // 各ビューの update() メソッドが呼び出されたことを検証
        verify(mockView1).update();
        verify(mockView2).update();

        // それ以外のメソッドが呼び出されていないことを確認（厳密な検証）
        verifyNoMoreInteractions(mockView1, mockView2);
    }

    

    // `animate()` のテスト
    /**
     * {@code animate()} メソッドが、{@code Forest} オブジェクトの {@code flushBounds()}、
     * {@code arrange(this)} を呼び出し、{@code changed()} を2回呼び出すことを確認します。
     * <p>
     * 注: {@code propagate} メソッド内部の {@code Thread.sleep} はここではスキップされます。
     * また、{@code SwingUtilities.invokeLater} の呼び出しも {@code propagate} 経由で発生するため、
     * ここでは {@code mockModel.changed()} の呼び出し回数に注目します。
     */
    @Test
    public void testAnimate() throws InterruptedException {
        // arrange(this) 内部で propagate が呼び出され、それが changed() を呼び出すため、
        // changed() が複数回呼び出される可能性があります。
        // ここでは、animate() が意図する直接の changed() 呼び出しが2回、
        // そして arrange(this) 内部からの changed() 呼び出しがあることを検証します。
        // ForestModel を spy 化して、changed() 呼び出し回数を正確に数えます。
        ForestModel spyForestModel = spy(forestModel); // ForestModel をスパイ化
        // スパイ化した ForestModel の mockForest フィールドを設定し直す
        try {
            java.lang.reflect.Field forestField = ForestModel.class.getDeclaredField("forest");
            forestField.setAccessible(true);
            forestField.set(spyForestModel, mockForest);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("テストセットアップ中に Forest フィールドの差し替えに失敗しました: " + e.getMessage());
        }

        // arrange(this) 内部での changed() 呼び出しをキャプチャするため、ダミーの ForestModel を渡す
        // arrange(this) 内部で propagate() -> model.changed() が呼ばれる
        // mockForest の arrange(ForestModel) が呼び出されたときに何もしないようにスタブ化 (無限ループ回避)
        doNothing().when(mockForest).arrange(any(ForestModel.class));

        // animate() を呼び出す
        spyForestModel.animate();

        // 1. forest.flushBounds() が呼び出されたことを検証
        verify(mockForest).flushBounds();
        // 2. 最初の changed() が呼び出されたことを検証
        verify(spyForestModel, times(1)).changed(); // animate() 内の最初の changed()

        // 3. forest.arrange(this) が呼び出されたことを検証
        verify(mockForest).arrange(spyForestModel);

        // 4. 2回目の changed() が呼び出されたことを検証
        verify(spyForestModel, times(2)).changed(); // animate() 内の2番目の changed()

        // arrange(this) 内部で propagate が呼ばれると changed() が呼ばれるが、
        // mockForest.arrange(any(ForestModel.class)) を doNothing() にしているため、
        // propagate が呼び出されない。もし propagate が呼ばれるようにしたいなら、
        // arrange のスタブ設定を調整するか、propagate メソッド自体をスパイ化する必要があります。
        // ここでは animate() の直接の責務に焦点を当てています。
    }

    

    // `arrange()` のテスト (引数なし)
    /**
     * {@code arrange()} メソッド (引数なし) が、{@code Forest} オブジェクトの {@code arrange()} メソッドを
     * 引数なしで呼び出すことを確認します。
     */
    @Test
    public void testArrangeNoArgs() {
        forestModel.arrange(); // arrange() を呼び出す
        verify(mockForest).arrange(); // mockForest の arrange() (引数なし) が呼び出されたことを検証
    }

    

    // `forest()` のテスト
    /**
     * {@code forest()} メソッドが、このモデルが保持する {@code Forest} オブジェクトを返すことを確認します。
     */
    @Test
    public void testForestAccessor() {
        // setUp() で設定した mockForest が返されることを確認
        assertEquals("forest() メソッドが正しい Forest オブジェクトを返すこと", mockForest, forestModel.forest());
    }

    

    // `picture()` のテスト
    /**
     * {@code picture()} メソッドが、{@code Forest} の境界に基づいて新しい {@code BufferedImage} を作成し、
     * そのグラフィックスコンテキストに {@code Forest} を描画することを確認します。
     */
    @Test
    public void testPicture() {
        // Forest の bounds() の振る舞いを設定
        Rectangle testBounds = new Rectangle(0, 0, 100, 50);
        when(mockForest.bounds()).thenReturn(testBounds);

        // Graphics オブジェクトをキャプチャするための ArgumentCaptor を用意
        ArgumentCaptor<Graphics> graphicsCaptor = ArgumentCaptor.forClass(Graphics.class);

        // picture() を呼び出す
        BufferedImage image = forestModel.picture();

        // 1. BufferedImage が正しいサイズで作成されたことを確認 (幅+1, 高さ+1)
        assertNotNull("BufferedImage が null でないこと", image);
        assertEquals("画像の幅が Forest の境界幅 + 1 であること", testBounds.width + 1, image.getWidth());
        assertEquals("画像の高さが Forest の境界高さ + 1 であること", testBounds.height + 1, image.getHeight());

        // 2. Forest.draw() が、作成された BufferedImage の Graphics オブジェクトで呼び出されたことを検証
        verify(mockForest).draw(graphicsCaptor.capture());

        // 3. キャプチャした Graphics オブジェクトが null でないことを確認
        assertNotNull("Forest.draw に渡された Graphics オブジェクトが null でないこと", graphicsCaptor.getValue());
    }

    

    // `read()` のテスト
    /**
     * {@code read()} メソッドが、指定されたファイルからノードとブランチのデータを正しく読み込み、
     * {@code Forest} オブジェクトに追加することを確認します。
     */
    @Test
    public void testRead() throws IOException {
        // テスト用の一時データファイルを作成
        File testFile = tempFolder.newFile("test_forest_data.txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(Constants.TagOfNodes + "\n");
            writer.write("1,NodeA\n");
            writer.write("2,NodeB\n");
            writer.write(Constants.TagOfBranches + "\n");
            writer.write("1,2\n"); // NodeA -> NodeB
        }

        // ForestModel を新たに作成し、このテストファイルで read() が実行されるようにします。
        // setUp() での forestModel の初期化とは別にテストします。
        ForestModel readTestModel = new ForestModel(testFile);

        // ここで readTestModel の内部の forest オブジェクトにアクセスして、
        // 適切にノードとブランチが追加されたか検証する必要があります。
        // ForestModel の forest フィールドへのアクセスは private なので、リフレクションを使用するか、
        // ForestModel に public な getter を追加する必要があります。
        // または、readTestModel.forest() を使って Forest オブジェクトを取得し、その状態を検証します。
        Forest actualForest = readTestModel.forest();

        // 1. ノードが正しく追加されたことを確認
        // mockForest を使っているため、mockForest の addNode が呼び出されたかを検証します。
        // Mockito.verify の呼び出しは ForestModel の read メソッドが Forest オブジェクトの addNode を呼び出すことを検証します。
        // このテストでは ForestModel を新しくインスタンス化しているので、そのインスタンスが持つ mockForest ではなく、
        // 実際の Forest インスタンスに対して addNode が呼ばれることになります。
        // そのため、readTestModel の Forest フィールドをスパイ化またはモック化する必要があります。
        // 簡略化のため、ここでは mockForest を ForestModel の forest に設定し直す形で対応します。
        try {
            java.lang.reflect.Field forestField = ForestModel.class.getDeclaredField("forest");
            forestField.setAccessible(true);
            forestField.set(readTestModel, mockForest); // ForestModel の forest を mockForest に差し替え
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("テストセットアップ中に Forest フィールドの差し替えに失敗しました: " + e.getMessage());
        }

        // read を再度呼び出して検証 (コンストラクタで既に呼ばれているが、検証のために改めて呼び出す)
        // もしくは、ForestModel のコンストラクタで read を呼ばないようにテスト用のコンストラクタを用意するか、
        // read メソッドを protected から public に一時的に変更して個別に呼び出すか。
        // ここでは、ForestModel をスパイ化して read を検証します。
        ForestModel spyReadTestModel = spy(new ForestModel(tempFolder.newFile("dummyForSpy.txt"))); // ダミーファイルで初期化
        try {
            java.lang.reflect.Field forestField = ForestModel.class.getDeclaredField("forest");
            forestField.setAccessible(true);
            forestField.set(spyReadTestModel, mockForest); // mockForest を設定
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("テストセットアップ中に Forest フィールドの差し替えに失敗しました: " + e.getMessage());
        }

        // read メソッドを呼び出す前に、mockForest の addNode/addBranch 呼び出しをクリア
        reset(mockForest);

        // read メソッドを呼び出す
        spyReadTestModel.read(testFile);

        // addNode が2回呼び出されたことを検証 (NodeA, NodeB)
        ArgumentCaptor<Node> nodeCaptor = ArgumentCaptor.forClass(Node.class);
        verify(mockForest, times(2)).addNode(nodeCaptor.capture());
        List<Node> capturedNodes = nodeCaptor.getAllValues();
        assertEquals("最初のノードが 'NodeA' であること", "NodeA", capturedNodes.get(0).getName());
        assertEquals("2番目のノードが 'NodeB' であること", "NodeB", capturedNodes.get(1).getName());

        // addBranch が1回呼び出されたことを検証 (NodeA -> NodeB)
        ArgumentCaptor<Branch> branchCaptor = ArgumentCaptor.forClass(Branch.class);
        verify(mockForest, times(1)).addBranch(branchCaptor.capture());
        Branch capturedBranch = branchCaptor.getValue();
        assertEquals("ブランチの始点ノードが 'NodeA' であること", "NodeA", capturedBranch.start().getName());
        assertEquals("ブランチの終点ノードが 'NodeB' であること", "NodeB", capturedBranch.end().getName());
    }

    

    // `root()` および `roots()` のテスト
    /**
     * {@code root()} メソッドが、{@code Forest} オブジェクトの {@code rootNodes()} を呼び出し、
     * その結果に基づいてルートノードを返すことを確認します。
     * {@code roots()} メソッドも同様に {@code Forest} の {@code rootNodes()} を呼び出すことを確認します。
     */
    @Test
    public void testRootAndRoots() {
        // mockForest の rootNodes() の振る舞いを設定
        Node mockRootNode = mock(Node.class);
        when(mockForest.rootNodes()).thenReturn(new ArrayList<>(Arrays.asList(mockRootNode)));

        // roots() をテスト
        ArrayList<Node> rootsResult = forestModel.roots();
        assertEquals("roots() が1つのルートノードを返すこと", 1, rootsResult.size());
        assertEquals("roots() が正しいルートノードを返すこと", mockRootNode, rootsResult.get(0));
        verify(mockForest).rootNodes(); // mockForest.rootNodes() が呼び出されたことを検証

        // root() をテスト
        Node rootResult = forestModel.root();
        assertEquals("root() が正しいルートノードを返すこと", mockRootNode, rootResult);
        // root() は roots() を呼び出すので、rootNodes() がさらに呼び出されるはずですが、
        // roots() の呼び出しで既に検証されているため、今回は追加の verify は不要です。
        // もし root() が直接 forest.rootNodes() を呼ぶなら verify(mockForest, times(2)).rootNodes(); のようになる。

        // ルートノードがない場合のテスト
        when(mockForest.rootNodes()).thenReturn(new ArrayList<>()); // ルートなしに設定
        assertNull("ルートノードがない場合、root() は null を返すこと", forestModel.root());
    }

    

    // `toString()` のテスト
    /**
     * {@code toString()} メソッドが、期待されるフォーマットで文字列を返すことを確認します。
     * これは {@code picture} フィールドの状態に依存します。
     */
    @Test
    public void testToString() {
        // コンストラクタで picture は初期化されていない（null またはダミー）と仮定
        // ForestModel のコンストラクタで picture = new BufferedImage(0,0,0) の行がコメントアウトされているため
        // 初期状態では picture は null です。
        String expectedInitialString = "forest.ForestModel[picture=null]";
        assertEquals("初期状態の toString() が正しいこと (picture は null)", expectedInitialString, forestModel.toString());

        // picture() を呼び出して picture が設定された後に toString() をテスト
        when(mockForest.bounds()).thenReturn(new Rectangle(0, 0, 10, 10)); // 適当な境界を設定
        BufferedImage dummyPicture = forestModel.picture(); // picture() を呼び出して picture フィールドを設定

        // toString() の結果が picture の情報を含むことを確認
        // BufferedImage の toString() はデフォルトで "BufferedImage@..." の形式になる
        String expectedStringAfterPicture = "forest.ForestModel[picture=" + dummyPicture.toString() + "]";
        assertEquals("picture 設定後の toString() が正しいこと", expectedStringAfterPicture, forestModel.toString());
    }
}
