package forest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * {@code Forest} アプリケーションの結合テストクラスです。
 * 実際のデータファイルを使用して {@code Forest} のコアロジックをテストします。
 * {@code System.exit()} の呼び出しを防ぐための {@code NoExitSecurityManager} を使用していましたが、
 * 最新のJavaバージョンでの非互換性のため、SecurityManager の設定を削除します。
 */
@RunWith(MockitoJUnitRunner.class)
public class ForestIntegrationTest {

    @Before
    public void setUp() {
        // 現在のJavaバージョンでは SecurityManager の設定はサポートされていません。
        // 詳細なログやデバッグが必要な場合は、ここに処理を追加できます。
    }

    /**
     * 指定されたファイルパスから Forest オブジェクトをロードおよびパースするヘルパーメソッドです。
     * このメソッドは、提供されたデータファイルの具体的なフォーマットに従って実装する必要があります。
     * 現在はプレースホルダーのロジックです。
     *
     * @param fileName リソース内のデータファイルのパス (例: "data/tree.txt")
     * @return ロードされたノードとブランチを持つ Forest オブジェクト
     * @throws Exception ファイルの読み込みまたはパース中にエラーが発生した場合
     */
    private Forest loadForestFromFile(String fileName, long sleepTickOverride) throws Exception { // sleepTickOverride 引数を追加
        Forest forest = new Forest(sleepTickOverride); // 追加: 新しいコンストラクタを使用

        List<String> lines = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            lines = reader.lines().collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource file: " + fileName, e);
        }

        List<Node> nodes = new ArrayList<>();
        List<Branch> branches = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.contains("->")) {
                String[] parts = line.split("->");
                String startNodeName = parts[0].trim();
                String endNodeName = parts[1].trim();

                Node startNode = nodes.stream()
                                      .filter(n -> n.getName().equals(startNodeName))
                                      .findFirst()
                                      .orElseGet(() -> {
                                          Node newNode = new Node(startNodeName);
                                          newNode.setLocation(new Point(0,0));
                                          newNode.setExtent(new Point(80, 30));
                                          forest.addNode(newNode);
                                          nodes.add(newNode);
                                          return newNode;
                                      });
                Node endNode = nodes.stream()
                                    .filter(n -> n.getName().equals(endNodeName))
                                    .findFirst()
                                    .orElseGet(() -> {
                                        Node newNode = new Node(endNodeName);
                                        newNode.setLocation(new Point(0,0));
                                        newNode.setExtent(new Point(80, 30));
                                        forest.addNode(newNode);
                                        nodes.add(newNode);
                                        return newNode;
                                    });
                forest.addBranch(new Branch(startNode, endNode));
                branches.add(new Branch(startNode, endNode));

            } else {
                String nodeName = line;
                if (nodes.stream().noneMatch(n -> n.getName().equals(nodeName))) {
                    Node newNode = new Node(nodeName);
                    newNode.setLocation(new Point(0,0));
                    newNode.setExtent(new Point(80, 30));
                    forest.addNode(newNode);
                    nodes.add(newNode);
                }
            }
        }
        return forest;
    }

    /**
     * 'data/tree.txt' を使用して Forest の結合テストを行います。
     * 整列後、フォレストのバウンドが正しく計算されることを確認します。
     */
    @Test
    public void testArrangeWithTreeData() throws Exception {
        System.out.println("\n--- testArrangeWithTreeData ---");
        Forest forest = loadForestFromFile("data/tree.txt", 1L); // 変更: スリープ時間を1ミリ秒に設定
        
        System.out.println("Nodes loaded: " + forest.getNodes().size());
        assertFalse("ツリーデータからノードがロードされていること", forest.getNodes().isEmpty());

        forest.arrange(null);

        System.out.println("--- After arrange() for Tree Data ---");
        for (Node node : forest.getNodes()) {
            System.out.println("Node: " + node.getName() + ", Location: " + node.getLocation() + ", Extent: " + node.getExtent());
        }
        System.out.println("Forest Bounds: " + forest.bounds());

        assertNotNull("整列後にboundsがnullでないこと", forest.getBounds());
        assertTrue("整列後にboundsの幅が0より大きいこと", forest.getBounds().width > 0);
        assertTrue("整列後にboundsの高さが0より大きいこと", forest.getBounds().height > 0);
    }

    /**
     * 'data/forest.txt' を使用して Forest の結合テストを行います。
     */
    @Test
    public void testArrangeWithForestData() throws Exception {
        System.out.println("\n--- testArrangeWithForestData ---");
        Forest forest = loadForestFromFile("data/forest.txt", 1L); // 変更: スリープ時間を1ミリ秒に設定

        System.out.println("Nodes loaded: " + forest.getNodes().size());
        assertFalse("フォレストデータからノードがロードされていること", forest.getNodes().isEmpty());
        forest.arrange(null);

        System.out.println("--- After arrange() for Forest Data ---");
        for (Node node : forest.getNodes()) {
            System.out.println("Node: " + node.getName() + ", Location: " + node.getLocation() + ", Extent: " + node.getExtent());
        }
        System.out.println("Forest Bounds: " + forest.bounds());

        assertNotNull("整列後にboundsがnullでないこと", forest.getBounds());
        assertTrue("整列後にboundsの幅が0より大きいこと", forest.getBounds().width > 0);
        assertTrue("整列後にboundsの高さが0より大きいこと", forest.getBounds().height > 0);
    }

    /**
     * 'data/semilattice.txt' を使用して Forest の結合テストを行います。
     */
    @Test
    public void testArrangeWithSemilatticeData() throws Exception {
        System.out.println("\n--- testArrangeWithSemilatticeData ---");
        Forest forest = loadForestFromFile("data/semilattice.txt", 1L); // 変更: スリープ時間を1ミリ秒に設定

        System.out.println("Nodes loaded: " + forest.getNodes().size());
        assertFalse("半束データからノードがロードされていること", forest.getNodes().isEmpty());
        forest.arrange(null);

        System.out.println("--- After arrange() for Semilattice Data ---");
        for (Node node : forest.getNodes()) {
            System.out.println("Node: " + node.getName() + ", Location: " + node.getLocation() + ", Extent: " + node.getExtent());
        }
        System.out.println("Forest Bounds: " + forest.bounds());

        assertNotNull("整列後にboundsがnullでないこと", forest.getBounds());
        assertTrue("整列後にboundsの幅が0より大きいこと", forest.getBounds().width > 0);
        assertTrue("整列後にboundsの高さが0より大きいこと", forest.getBounds().height > 0);
    }
}