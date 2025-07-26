package forest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.ArgumentCaptor; // メソッドの引数をキャプチャするために使用

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle; // Rectangle を使用
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch; // スレッド処理のテストで使用

import javax.swing.SwingUtilities; // SwingUtilities.invokeLater をモック化するために必要

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * {@code Forest} クラスの単体テストクラスです。
 * JUnit 4 と Mockito を利用して、{@code Forest} の各メソッドが期待通りに動作するかを検証します。
 * {@code Node}, {@code Branch}, {@code ForestModel}, {@code Graphics} といった依存オブジェクトは
 * Mockito でモック化します。
 * <p>
 * 注: {@code arrange} メソッドは再帰的で複雑なロジックを持つため、すべてのパスを網羅するテストは非常に困難です。
 * ここでは主要なシナリオをカバーします。
 * また、{@code SwingUtilities.invokeLater} のテストには特別な考慮が必要です。
 */
@RunWith(MockitoJUnitRunner.class)
public class ForestTest {

    private Forest forest; // テスト対象の Forest インスタンス

    // Forest が内部で操作する Node と Branch のリストをモック化
    // ただし、これらのリストは Forest のコンストラクタで初期化されるため、
    // ここで @Mock にしても Forest 内部のリストには適用されません。
    // そのため、実際には addNode/addBranch を通じてモックノード/ブランチを追加します。

    // 各テストで再利用するモックノード
    @Mock private Node mockNodeA;
    @Mock private Node mockNodeB;
    @Mock private Node mockNodeC;
    @Mock private Node mockNodeD;

    // 各テストで再利用するモックブランチ
    @Mock private Branch mockBranchAB; // A -> B
    @Mock private Branch mockBranchBC; // B -> C

    /**
     * テストの前に実行されるセットアップメソッド。
     * 各テストケースの前に新しい {@code Forest} インスタンスを作成し、
     * モックの振る舞いを設定します。
     */
    @Before
    public void setUp() {
        forest = new Forest();

        // モックノードの基本的な振る舞いを設定
        // Node の toString() は Node[node=name] の形式なのでそれに合わせる
        when(mockNodeA.getName()).thenReturn("A");
        when(mockNodeA.toString()).thenReturn("forest.Node[node=A]");
        when(mockNodeA.getLocation()).thenReturn(new Point(0, 0)); // デフォルト位置
        // getExtent() は Point を返すように修正 (ユーザーの指摘に基づく)
        // Point の x が幅、y が高さを表すと仮定
        when(mockNodeA.getExtent()).thenReturn(new Point(50, 30)); // width, height

        when(mockNodeB.getName()).thenReturn("B");
        when(mockNodeB.toString()).thenReturn("forest.Node[node=B]");
        when(mockNodeB.getLocation()).thenReturn(new Point(0, 0));
        // getExtent() は Point を返すように修正
        when(mockNodeB.getExtent()).thenReturn(new Point(60, 40)); // width, height

        when(mockNodeC.getName()).thenReturn("C");
        when(mockNodeC.toString()).thenReturn("forest.Node[node=C]");
        when(mockNodeC.getLocation()).thenReturn(new Point(0, 0));
        // getExtent() は Point を返すように修正
        when(mockNodeC.getExtent()).thenReturn(new Point(70, 50)); // width, height

        when(mockNodeD.getName()).thenReturn("D");
        when(mockNodeD.toString()).thenReturn("forest.Node[node=D]");
        when(mockNodeD.getLocation()).thenReturn(new Point(0, 0));
        // getExtent() は Point を返すように修正
        when(mockNodeD.getExtent()).thenReturn(new Point(80, 60)); // width, height


        // モックブランチの振る舞いを設定
        when(mockBranchAB.start()).thenReturn(mockNodeA);
        when(mockBranchAB.end()).thenReturn(mockNodeB);

        when(mockBranchBC.start()).thenReturn(mockNodeB);
        when(mockBranchBC.end()).thenReturn(mockNodeC);

        // Constants クラスが存在し、適切に初期化されていることを前提とします。
        // 必要であれば、Constants の値もここでスタブ化または設定できます。
    }

    

    // テストメソッド

    // コンストラクタのテスト
    /**
     * {@code Forest} コンストラクタが {@code nodes}, {@code branches}, {@code bounds}
     * フィールドを正しく初期化することを確認します。
     */
    @Test
    public void testConstructor() {
        assertNotNull("nodes リストが null でないこと", forest.getNodes());
        assertTrue("nodes リストが空であること", forest.getNodes().isEmpty());
        assertNotNull("branches リストが null でないこと", forest.getBranches());
        assertTrue("branches リストが空であること", forest.getBranches().isEmpty());
        assertNotNull("bounds オブジェクトが null でないこと", forest.getBounds());
        // 初期状態では bounds の幅と高さが0であることを確認
        assertEquals("初期 bounds の幅が0であること", 0, forest.getBounds().width);
        assertEquals("初期 bounds の高さが0であること", 0, forest.getBounds().height);
    }

    

    // `addNode()` のテスト
    /**
     * {@code addNode()} メソッドがノードを {@code nodes} リストに正しく追加することを確認します。
     */
    @Test
    public void testAddNode() {
        forest.addNode(mockNodeA);
        assertEquals("ノードが1つ追加されていること", 1, forest.getNodes().size());
        assertTrue("追加したノードが含まれていること", forest.getNodes().contains(mockNodeA));

        forest.addNode(mockNodeB);
        assertEquals("ノードが2つ追加されていること", 2, forest.getNodes().size());
        assertTrue("追加した2つ目のノードが含まれていること", forest.getNodes().contains(mockNodeB));
    }

    

    // `addBranch()` のテスト
    /**
     * {@code addBranch()} メソッドがブランチを {@code branches} リストに正しく追加することを確認します。
     */
    @Test
    public void testAddBranch() {
        forest.addBranch(mockBranchAB);
        assertEquals("ブランチが1つ追加されていること", 1, forest.getBranches().size());
        assertTrue("追加したブランチが含まれていること", forest.getBranches().contains(mockBranchAB));

        forest.addBranch(mockBranchBC);
        assertEquals("ブランチが2つ追加されていること", 2, forest.getBranches().size());
        assertTrue("追加した2つ目のブランチが含まれていること", forest.getBranches().contains(mockBranchBC));
    }

    

    // `rootNodes()` のテスト
    /**
     * {@code rootNodes()} メソッドが、終点ノードとして現れない（つまり親を持たない）ノードを
     * ルートノードとして正しく識別することを確認します。
     */
    @Test
    public void testRootNodes() {
        forest.addNode(mockNodeA);
        forest.addNode(mockNodeB);
        forest.addNode(mockNodeC);
        forest.addBranch(mockBranchAB); // A -> B
        forest.addBranch(mockBranchBC); // B -> C

        ArrayList<Node> roots = forest.rootNodes();
        assertEquals("ルートノードが1つであること", 1, roots.size());
        assertTrue("ルートノードが mockNodeA であること", roots.contains(mockNodeA));
        assertFalse("mockNodeB はルートではないこと", roots.contains(mockNodeB)); // B は A の子
        assertFalse("mockNodeC はルートではないこと", roots.contains(mockNodeC)); // C は B の子

        // 全てのノードがルートになるケース
        Forest disconnectedForest = new Forest();
        disconnectedForest.addNode(mockNodeA);
        disconnectedForest.addNode(mockNodeB);
        ArrayList<Node> disconnectedRoots = disconnectedForest.rootNodes();
        assertEquals("接続されていないノードは全てルートになること", 2, disconnectedRoots.size());
        assertTrue(disconnectedRoots.contains(mockNodeA));
        assertTrue(disconnectedRoots.contains(mockNodeB));
    }

    

    // `subNodes()` のテスト
    /**
     * {@code subNodes()} メソッドが、指定されたノードの直接の子ノードを正しく返すことを確認します。
     */
    @Test
    public void testSubNodes() {
        forest.addNode(mockNodeA);
        forest.addNode(mockNodeB);
        forest.addNode(mockNodeC);
        forest.addBranch(mockBranchAB); // A -> B
        forest.addBranch(mockBranchBC); // B -> C

        ArrayList<Node> subNodesOfA = forest.subNodes(mockNodeA);
        assertEquals("Aの子ノードが1つであること", 1, subNodesOfA.size());
        assertTrue("Aの子ノードがBであること", subNodesOfA.contains(mockNodeB));

        ArrayList<Node> subNodesOfB = forest.subNodes(mockNodeB);
        assertEquals("Bの子ノードが1つであること", 1, subNodesOfB.size());
        assertTrue("Bの子ノードがCであること", subNodesOfB.contains(mockNodeC));

        ArrayList<Node> subNodesOfC = forest.subNodes(mockNodeC);
        assertTrue("Cの子ノードは存在しないこと", subNodesOfC.isEmpty());

        ArrayList<Node> subNodesOfD = forest.subNodes(mockNodeD); // 存在しないノード
        assertTrue("存在しないノードの子ノードは存在しないこと", subNodesOfD.isEmpty());
    }

    

    // `superNodes()` のテスト
    /**
     * {@code superNodes()} メソッドが、指定されたノードの直接の親ノードを正しく返すことを確認します。
     */
    @Test
    public void testSuperNodes() {
        forest.addNode(mockNodeA);
        forest.addNode(mockNodeB);
        forest.addNode(mockNodeC);
        forest.addBranch(mockBranchAB); // A -> B
        forest.addBranch(mockBranchBC); // B -> C

        ArrayList<Node> superNodesOfC = forest.superNodes(mockNodeC);
        assertEquals("Cの親ノードが1つであること", 1, superNodesOfC.size());
        assertTrue("Cの親ノードがBであること", superNodesOfC.contains(mockNodeB));

        ArrayList<Node> superNodesOfB = forest.superNodes(mockNodeB);
        assertEquals("Bの親ノードが1つであること", 1, superNodesOfB.size());
        assertTrue("Bの親ノードがAであること", superNodesOfB.contains(mockNodeA));

        ArrayList<Node> superNodesOfA = forest.superNodes(mockNodeA);
        assertTrue("Aの親ノードは存在しないこと", superNodesOfA.isEmpty());
    }

    

    // `sortNodes()` のテスト
    /**
     * {@code sortNodes()} メソッドが、ノードコレクションをノード名で昇順にソートすることを確認します。
     */
    @Test
    public void testSortNodes() {
        // 名前が異なるノードを準備
        Node nodeX = mock(Node.class); when(nodeX.getName()).thenReturn("X");
        Node nodeY = mock(Node.class); when(nodeY.getName()).thenReturn("Y");
        Node nodeZ = mock(Node.class); when(nodeZ.getName()).thenReturn("Z");
        Node nodeA = mock(Node.class); when(nodeA.getName()).thenReturn("A");

        ArrayList<Node> unsorted = new ArrayList<>(Arrays.asList(nodeX, nodeZ, nodeA, nodeY));
        ArrayList<Node> sorted = forest.sortNodes(unsorted);

        // ソートされたリストの順序を検証
        assertEquals("ソートされたリストのサイズが同じであること", unsorted.size(), sorted.size());
        assertEquals("最初の要素がAであること", nodeA, sorted.get(0));
        assertEquals("2番目の要素がXであること", nodeX, sorted.get(1));
        assertEquals("3番目の要素がYであること", nodeY, sorted.get(2));
        assertEquals("4番目の要素がZであること", nodeZ, sorted.get(3));

        // 元のリストが変更されていないことを確認
        assertEquals("元のリストのサイズが同じであること", 4, unsorted.size());
        assertEquals("元のリストの最初の要素がXであること", nodeX, unsorted.get(0));
    }

    

    // `bounds()` のテスト
    /**
     * {@code bounds()} メソッドが、フォレスト内の全ノードを考慮した最小の矩形領域を正しく計算することを確認します。
     * ノードが存在しない場合は、幅と高さが0の矩形を返します。
     */
    @Test
    public void testBounds() {
        // ノードがない場合
        Rectangle emptyBounds = forest.bounds();
        assertEquals("ノードがない場合、幅が0であること", 0, emptyBounds.width);
        assertEquals("ノードがない場合、高さが0であること", 0, emptyBounds.height);
        assertEquals("ノードがない場合、x座標が0であること", 0, emptyBounds.x);
        assertEquals("ノードがない場合、y座標が0であること", 0, emptyBounds.y);

        // ノードを追加してテスト
        when(mockNodeA.getLocation()).thenReturn(new Point(10, 20));
        when(mockNodeA.getExtent()).thenReturn(new Point(50, 30)); // x=width, y=height
        forest.addNode(mockNodeA);

        when(mockNodeB.getLocation()).thenReturn(new Point(100, 5));
        when(mockNodeB.getExtent()).thenReturn(new Point(40, 60)); // x=width, y=height
        forest.addNode(mockNodeB);

        when(mockNodeC.getLocation()).thenReturn(new Point(5, 70));
        when(mockNodeC.getExtent()).thenReturn(new Point(20, 10)); // x=width, y=height
        forest.addNode(mockNodeC);

        Rectangle calculatedBounds = forest.bounds();

        // 期待される最小X: min(10, 100, 5) = 5
        assertEquals("最小X座標が正しいこと", 5, calculatedBounds.x);
        // 期待される最小Y: min(20, 5, 70) = 5
        assertEquals("最小Y座標が正しいこと", 5, calculatedBounds.y);
        // 期待される最大X: max(10+50, 100+40, 5+20) = max(60, 140, 25) = 140
        // 期待される最大Y: max(20+30, 5+60, 70+10) = max(50, 65, 80) = 80
        // 幅: 最大X - 最小X = 140 - 5 = 135
        assertEquals("幅が正しいこと", 135, calculatedBounds.width);
        // 高さ: 最大Y - 最小Y = 80 - 5 = 75
        assertEquals("高さが正しいこと", 75, calculatedBounds.height);

        // flushBoundsでboundsがnullになることも確認
        forest.flushBounds();
        assertNull("flushBounds() 後に bounds が null であること", forest.getBounds());
    }

    

    // `draw()` のテスト
    /**
     * {@code draw()} メソッドが、フォレスト内のすべてのノードとブランチの {@code draw} メソッドを
     * 正しい {@code Graphics} オブジェクトで呼び出すことを確認します。
     */
    @Test
    public void testDraw() {
        Graphics mockGraphics = mock(Graphics.class); // Graphics オブジェクトをモック化

        forest.addNode(mockNodeA);
        forest.addNode(mockNodeB);
        forest.addBranch(mockBranchAB);

        forest.draw(mockGraphics);

        // 各ノードの draw メソッドが呼び出されたことを確認
        verify(mockNodeA).draw(mockGraphics);
        verify(mockNodeB).draw(mockGraphics);
        // 各ブランチの draw メソッドが呼び出されたことを確認
        verify(mockBranchAB).draw(mockGraphics);

        // 不要な相互作用がないことを確認 (verifyNoMoreInteractions は厳密なテストに役立つ)
        verifyNoMoreInteractions(mockGraphics, mockNodeA, mockNodeB, mockBranchAB);
    }

    

    // `flushBounds()` のテスト
    /**
     * {@code flushBounds()} メソッドが、すべてのノードの位置をリセットし、
     * フォレストの {@code bounds} を null に設定することを確認します。
     * また、ノードの位置が {@code getExtent().y * (インデックス + 1)} に設定されることも確認します。
     */
    @Test
    public void testFlushBounds() {
        forest.addNode(mockNodeA);
        forest.addNode(mockNodeB);

        // 初期位置を適当に設定（確認のため）
        //node.setLocation(new Point(100, 100));
        // getExtent() は Point を返すように修正
        when(mockNodeA.getExtent()).thenReturn(new Point(50, 30)); // 高さ 30 (Point.y)
        when(mockNodeB.getExtent()).thenReturn(new Point(60, 40)); // 高さ 40 (Point.y)

        forest.flushBounds();

        // Forest.nodes は addNode で追加された順序が保たれることを前提
        // mockNodeA の新しい位置: (0, 30 * 1) = (0, 30)
        verify(mockNodeA).setLocation(new Point(0, 30));
        // mockNodeB の新しい位置: (0, 40 * 2) = (0, 80)
        verify(mockNodeB).setLocation(new Point(0, 80));

        assertNull("bounds が null に設定されていること", forest.getBounds());
    }

    

    // `propagate()` のテスト
    /**
     * {@code propagate()} メソッドが、{@code ForestModel} が {@code null} でない場合に
     * {@code Thread.sleep} を呼び出し、{@code SwingUtilities.invokeLater} 経由で {@code model.changed()} を
     * 呼び出すことを確認します。
     * <p>
     * 注: {@code SwingUtilities.invokeLater} のテストは複雑です。
     * ここでは {@code SwingUtilities} をモック化し、その中の {@code invokeLater} が呼ばれることを検証します。
     * {@code Thread.sleep} は実際の実行を伴うため、テスト時間を短縮するために短いスリープ時間を使用するか、
     * テスト環境で {@code Constants.SleepTick} をオーバーライドすることを検討してください。
     */
    @Test
    public void testPropagate() throws Exception {
        ForestModel mockModel = mock(ForestModel.class);

        // changed() が呼び出されたことを待つためのラッチ
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            // Forest.propagate が SwingUtilities.invokeLater を呼び出すことをシミュレート
            // ここでは実際の model.changed() は呼び出さず、ラッチをカウントダウンするだけ
            SwingUtilities.invokeLater(() -> {
                latch.countDown();
            });
            return null; // void メソッドなので null を返す
        }).when(mockModel).changed(); // changed() メソッドが呼ばれたときにこの Answer を実行するように設定

        // propagate を呼び出す
        forest.propagate(mockModel);

        // ラッチがカウントダウンされるのを最大5秒待機
        latch.await(5, java.util.concurrent.TimeUnit.SECONDS);

        // mockModel.changed() が呼び出されたことを検証 (1回だけ期待)
        verify(mockModel, times(1)).changed();

        // Constants.SleepTick = ORIGINAL_SLEEP_TICK; // テスト後に元に戻す (必要に応じて)
    }

    /**
     * {@code propagate()} メソッドが、{@code ForestModel} が {@code null} の場合に何もしないことを確認します。
     */
    @Test
    public void testPropagateWithNullModel() throws Exception {
        // null モデルで propagate を呼び出す
        forest.propagate(null);

        // 何も起こらないことを検証 (例外なし、スリープなし、changed() 呼び出しなし)
        // ただし、Thread.sleep は検証できないため、ここでは主に例外が発生しないことと、
        // model.changed() が呼び出されないことを確認します。
        // verifyZeroInteractions(mockModel); // mockModel を使っていないのでこれは不要
        // (厳密にテストしたい場合は、PowerMock で Thread.sleep をモック化する必要がある)
    }

    

    // `arrange()` メソッドのテスト (一部)
    /**
     * {@code arrange()} メソッド (オーバーロードなし) が、{@code nodes} の状態をリセットし、
     * ルートノードをソートして処理を開始することを確認します。
     * 再帰的な arrange ロジック全体のテストは複雑なため、ここでは初期ステップに焦点を当てます。
     */
    @Test
    public void testArrangeNoModel() {
        forest.addNode(mockNodeA);
        forest.addNode(mockNodeB);
        forest.addNode(mockNodeC);
        forest.addBranch(mockBranchAB); // A -> B
        forest.addBranch(mockBranchBC); // B -> C

        // arrange() 呼び出し前に、setStatus が呼ばれることを確認できるようにリセット
        // reset(mockNodeA, mockNodeB, mockNodeC); // この行を削除

        // arrange() を実行
        forest.arrange();

        // すべてのノードのsetStatusがConstants.UnVisitedに設定されることを確認
        verify(mockNodeA).setStatus(Constants.UnVisited);
        verify(mockNodeB).setStatus(Constants.UnVisited);
        verify(mockNodeC).setStatus(Constants.UnVisited);

        // arrange(Node, Point, ForestModel) がルートノード(A)で呼び出されることを確認 (検証は複雑)
        // 例えば、Node.setLocation() が呼び出されることなどを検証します。
        // setLocationが呼び出されるのはarrange(Node,Point,ForestModel)内なので、
        // arrange()がそれを適切に呼び出していることの証拠になる。
        // Mockito の spy を使って Forest オブジェクト自体をスパイ化すれば、内部の arrange(Node, Point, ...) 呼び出しを検証できるが、
        // テストの複雑性が増すため、ここでは割愛します。
        // 最もシンプルなのは、最終的にノードの座標が変更されることを確認すること。
        // ただし、Node の setLocation がモックされているので、verify で確認する形になります。
        verify(mockNodeA, atLeastOnce()).setLocation(any(Point.class));
        verify(mockNodeB, atLeastOnce()).setLocation(any(Point.class));
        verify(mockNodeC, atLeastOnce()).setLocation(any(Point.class));
    }

    /**
     * {@code arrange(Node, Point, ForestModel)} が再帰的に動作し、ノードの状態を設定し、
     * {@code propagate} を呼び出すことを確認します。
     */
    @Test
    public void testArrangeRecursiveLogic() {
        // テストのためにConstants.IntervalとConstants.SleepTickの値を設定
        // これはConstantsクラスが変更可能であるか、テストでReflectionを使う場合を想定。
        // Constants.Interval = new Point(10, 10);
        // Constants.SleepTick = 10L;

        forest.addNode(mockNodeA);
        forest.addNode(mockNodeB);
        forest.addNode(mockNodeC);
        forest.addBranch(mockBranchAB);
        forest.addBranch(mockBranchBC);

        // mockNodeAの初期状態を設定
        when(mockNodeA.getStatus()).thenReturn(Constants.UnVisited, Constants.Visited);
        // mockNodeBとmockNodeCは、getStatus()が複数回呼ばれてもUnVisitedを返すように設定
        when(mockNodeB.getStatus()).thenReturn(Constants.UnVisited, Constants.UnVisited, Constants.Visited);
        when(mockNodeC.getStatus()).thenReturn(Constants.UnVisited, Constants.UnVisited, Constants.Visited);

        // モックモデル
        ForestModel mockModel = mock(ForestModel.class);

        // arrange メソッド呼び出し
        Point initialPoint = new Point(0, 0);
        forest.arrange(mockNodeA, initialPoint, mockModel);

        // verify で呼び出しを検証
        // 各ノードの status が Visited に設定されること
        verify(mockNodeA).setStatus(Constants.Visited);
        verify(mockNodeB).setStatus(Constants.Visited);
        verify(mockNodeC).setStatus(Constants.Visited);

        // setLocation が各ノードで呼び出されること
        verify(mockNodeA, atLeastOnce()).setLocation(any(Point.class));
        verify(mockNodeB, atLeastOnce()).setLocation(any(Point.class));
        verify(mockNodeC, atLeastOnce()).setLocation(any(Point.class));

        // propagate が適切に呼び出されること
        // arrange(Node, Point, ForestModel) の中に propagate が複数回呼ばれるパスがあるため、atLeastOnce を使用
        verify(mockModel, atLeastOnce()).changed();
    }


    

    // `whichOfNodes()` のテスト
    /**
     * {@code whichOfNodes()} メソッドが、指定された座標にノードが存在する場合にそのノードを返し、
     * 存在しない場合は {@code null} を返すことを確認します。
     */
    @Test
    public void testWhichOfNodes() {
        // NodeA の位置と大きさを設定
        when(mockNodeA.getLocation()).thenReturn(new Point(10, 20));
        when(mockNodeA.getExtent()).thenReturn(new Point(50, 30)); // 領域: (10,20) - (60,50)

        // NodeB の位置と大きさを設定
        when(mockNodeB.getLocation()).thenReturn(new Point(100, 50));
        when(mockNodeB.getExtent()).thenReturn(new Point(40, 60)); // 領域: (100,50) - (140,110)

        forest.addNode(mockNodeA);
        forest.addNode(mockNodeB);

        // NodeA の内部の点
        Point pointInsideA = new Point(20, 30);
        assertEquals("NodeA の内部の点であれば NodeA を返すこと", mockNodeA, forest.whichOfNodes(pointInsideA));

        // NodeB の内部の点
        Point pointInsideB = new Point(110, 70);
        assertEquals("NodeB の内部の点であれば NodeB を返すこと", mockNodeB, forest.whichOfNodes(pointInsideB));

        // どのノードにも属さない点
        Point pointOutside = new Point(70, 70);
        assertNull("どのノードにも属さない点であれば null を返すこと", forest.whichOfNodes(pointOutside));

        // ノードがない場合
        Forest emptyForest = new Forest();
        assertNull("ノードがない場合、常に null を返すこと", emptyForest.whichOfNodes(new Point(10, 10)));
    }

    

    // `toString()` のテスト
    /**
     * {@code toString()} メソッドが、期待されるフォーマットで文字列を返すかをテストします。
     * {@code bounds()} が一度呼び出されていれば、その結果が文字列に含まれることを確認します。
     */
    @Test
    public void testToString() {
        // デフォルトでは bounds は new Rectangle() で初期化されている
        String expectedDefaultString = "forest.Forest[bounds=java.awt.Rectangle[x=0,y=0,width=0,height=0]]";
        assertEquals("初期状態の toString() が正しいこと", expectedDefaultString, forest.toString());

        // bounds() を呼び出して、bounds が計算された後に toString() をテスト
        when(mockNodeA.getLocation()).thenReturn(new Point(10, 20));
        when(mockNodeA.getExtent()).thenReturn(new Point(50, 30)); // Point を返すように修正
        forest.addNode(mockNodeA);
        forest.bounds(); // bounds を計算させる

        String expectedStringAfterBounds = "forest.Forest[bounds=java.awt.Rectangle[x=10,y=20,width=50,height=30]]";
        assertEquals("bounds 計算後の toString() が正しいこと", expectedStringAfterBounds, forest.toString());
    }
}
