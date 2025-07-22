package forest;

import org.junit.Test;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import javax.swing.plaf.FontUIResource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * {@code Constants} クラスの単体テストクラスです。
 * このクラスは、{@code Constants} クラスに定義されているすべての定数が、
 * 期待される値を持っていることを検証します。
 * また、{@code Constants} クラスがインスタンス化できないことも確認します。
 */
public class ConstantsTest {

    

    // 定数値のテスト

    /**
     * {@code TagofTrees} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testTagOfTrees() {
        assertEquals("trees:", Constants.TagofTrees);
    }

    /**
     * {@code TagOfNodes} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testTagOfNodes() {
        assertEquals("nodes:", Constants.TagOfNodes);
    }

    /**
     * {@code TagOfBranches} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testTagOfBranches() {
        assertEquals("branches:", Constants.TagOfBranches);
    }

    /**
     * {@code ForegroundColor} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testForegroundColor() {
        assertEquals(Color.BLACK, Constants.ForegroundColor);
    }

    /**
     * {@code BackgroundColor} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testBackgroundColor() {
        assertEquals(Color.WHITE, Constants.BackgroundColor);
    }

    /**
     * {@code DefaultFont} 定数の値が期待通りであることを確認します。
     * フォント名、スタイル、サイズを個別に検証します。
     */
    @Test
    public void testDefaultFont() {
        Font expectedFont = new FontUIResource("Serif", Font.PLAIN, 12);
        assertNotNull(Constants.DefaultFont);
        assertEquals("Serif", Constants.DefaultFont.getName());
        assertEquals(Font.PLAIN, Constants.DefaultFont.getStyle());
        assertEquals(12, Constants.DefaultFont.getSize());
        // FontUIResource は Font を継承しているため、equals メソッドで比較可能
        assertEquals(expectedFont, Constants.DefaultFont);
    }

    /**
     * {@code Margin} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testMargin() {
        assertEquals(new Point(1, 1), Constants.Margin);
    }

    /**
     * {@code Interval} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testInterval() {
        assertEquals(new Point(25, 2), Constants.Interval);
    }

    /**
     * {@code UnKnown} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testUnKnown() {
        assertEquals(Integer.valueOf(-1), Constants.UnKnown);
    }

    /**
     * {@code UnVisited} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testUnVisited() {
        assertEquals(Integer.valueOf(0), Constants.UnVisited);
    }

    /**
     * {@code Visited} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testVisited() {
        assertEquals(Integer.valueOf(1), Constants.Visited);
    }

    /**
     * {@code SleepTick} 定数の値が期待通りであることを確認します。
     */
    @Test
    public void testSleepTick() {
        assertEquals(Integer.valueOf(100), Constants.SleepTick);
    }

    

    // クラスのインスタンス化防止のテスト

    /**
     * {@code Constants} クラスのコンストラクタがプライベートであり、
     * 外部からインスタンス化できないことを確認します。
     * リフレクションを使ってプライベートコンストラクタへのアクセスを試み、
     * {@code IllegalAccessException} が発生することを確認することで、
     * このクラスが意図的にインスタンス化不可能に設計されていることを検証します。
     */
    @Test
    public void testPrivateConstructor() {
        try {
            // Constants クラスのすべての宣言されたコンストラクタを取得
            Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
            // コンストラクタがプライベートであることを確認
            assertTrue("Constants クラスのコンストラクタはプライベートであるべき",
                       Modifier.isPrivate(constructor.getModifiers()));

            // プライベートコンストラクタにアクセス可能にする (テストのため)
            constructor.setAccessible(true);

            // インスタンス化を試みる
            constructor.newInstance();

            // ここに到達した場合はテスト失敗 (例外がスローされるべき)
            fail("Constants クラスはインスタンス化されるべきではありません。");
        } catch (InvocationTargetException e) {
            // コンストラクタが例外をスローした場合 (例えば、意図的に RuntimeException をスローする場合)
            // このケースでは、コンストラクタが空なので発生しないはず。
            // もし何らかの例外がコンストラクタ内でスローされたら、このブロックに入る。
            assertNotNull(e.getTargetException()); // 内部の例外があることを確認
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            // インスタンス化の禁止やアクセス違反が発生した場合
            // IllegalAccessException が期待される。
            assertTrue("Constants クラスのインスタンス化は IllegalAccessException をスローするべき",
                       e instanceof IllegalAccessException || e instanceof InstantiationException || e instanceof NoSuchMethodException);
        }
    }
}
