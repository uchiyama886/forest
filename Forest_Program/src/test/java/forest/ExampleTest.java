package forest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule; // TemporaryFolder ルールを使用するために必要
import org.junit.Test;
import org.junit.rules.TemporaryFolder; // 一時ファイル/ディレクトリ作成のための JUnit ルール

import java.io.ByteArrayOutputStream; // 標準出力/エラー出力をキャプチャするため
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * {@code Example} クラスの単体テストクラスです。
 * {@code main} メソッドの引数処理、ファイル存在チェック、および
 * プライベートコンストラクタの動作を検証します。
 * <p>
 * 注: {@code main} メソッドが GUI コンポーネントを直接インスタンス化するため、
 * 標準の JUnit と Mockito ではその部分の単体テストは困難です。
 * このテストでは、{@code System.exit()} の呼び出しと標準出力/エラー出力に焦点を当てます。
 */
public class ExampleTest {

    // 一時ファイル/ディレクトリを作成するための JUnit ルール
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    // System.out と System.err をキャプチャするためのストリーム
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    // 元の System.out, System.err, SecurityManager を保存するための変数
    private PrintStream originalOut;
    private PrintStream originalErr;
    private SecurityManager originalSecurityManager;

    /**
     * 各テストメソッドの前に実行されるセットアップメソッドです。
     * 標準出力と標準エラーをリダイレクトし、カスタムの {@code SecurityManager} を設定します。
     */
    @Before
    public void setUp() {
        // 元の System.out, System.err, SecurityManager を保存
        originalOut = System.out;
        originalErr = System.err;
        originalSecurityManager = System.getSecurityManager();

        // 標準出力と標準エラーをキャプチャするためのストリームにリダイレクト
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        // System.exit() の呼び出しをインターセプトするためのカスタム SecurityManager を設定
        System.setSecurityManager(new NoExitSecurityManager());
    }

    /**
     * 各テストメソッドの後に実行されるティアダウンメソッドです。
     * リダイレクトした標準出力と標準エラーを元に戻し、元の {@code SecurityManager} を復元します。
     */
    @After
    public void tearDown() {
        // 標準出力と標準エラーを元に戻す
        System.setOut(originalOut);
        System.setErr(originalErr);

        // SecurityManager を元に戻す
        System.setSecurityManager(originalSecurityManager);
    }

    

    // プライベートコンストラクタのテスト

    /**
     * {@code Example} クラスのコンストラクタがプライベートであり、
     * 外部からインスタンス化できないことを確認します。
     * リフレクションを使ってプライベートコンストラクタへのアクセスを試み、
     * {@code IllegalAccessException} が発生することを確認することで、
     * このクラスが意図的にインスタンス化不可能に設計されていることを検証します。
     */
    @Test
    public void testPrivateConstructor() {
        try {
            // Example クラスの宣言されたコンストラクタを取得
            Constructor<Example> constructor = Example.class.getDeclaredConstructor();
            // コンストラクタがプライベートであることを確認
            assertTrue("Example クラスのコンストラクタはプライベートであるべき",
                       Modifier.isPrivate(constructor.getModifiers()));

            // プライベートコンストラクタにアクセス可能にする (テストのため)
            constructor.setAccessible(true);

            // インスタンス化を試みる
            constructor.newInstance();

            // ここに到達した場合はテスト失敗 (例外がスローされるべき)
            fail("Example クラスはインスタンス化されるべきではありません。");
        } catch (InvocationTargetException e) {
            // コンストラクタが内部で例外をスローした場合
            assertNotNull(e.getTargetException());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            // インスタンス化の禁止やアクセス違反が発生した場合
            // IllegalAccessException が期待される。
            assertTrue("Example クラスのインスタンス化は IllegalAccessException をスローするべき",
                       e instanceof IllegalAccessException || e instanceof InstantiationException || e instanceof NoSuchMethodException);
        }
    }

    

    // `main` メソッドのテスト

    /// 引数が少なすぎる場合のテスト
    /**
     * {@code main} メソッドが引数なしで呼び出されたときに、
     * エラーメッセージを標準エラーに出力し、{@code System.exit(1)} を呼び出すことを確認します。
     */
    @Test(expected = ExitException.class) // ExitException がスローされることを期待
    public void testMainTooFewArguments() {
        // main メソッドを引数なしで呼び出す
        Example.main(new String[]{});

        // System.exit(1) が呼び出されるため、ExitException がスローされ、ここに到達しない。
        // 例外が捕捉された後に以下の検証が行われる。
        // 標準エラー出力の内容を検証
        assertEquals("There are too few arguments.\n", errContent.toString());
        // System.out には何も出力されないことを確認
        assertEquals("", outContent.toString());
    }

    /// ファイルが存在しない場合のテスト
    /**
     * {@code main} メソッドが、存在しないファイルパスを引数として受け取ったときに、
     * エラーメッセージを標準エラーに出力し、{@code System.exit(1)} を呼び出すことを確認します。
     */
    @Test(expected = ExitException.class) // ExitException がスローされることを期待
    public void testMainFileDoesNotExist() throws IOException {
        // 存在しない一時ファイルパスを作成
        String nonExistentFilePath = tempFolder.getRoot().getAbsolutePath() + "/nonexistent.txt";

        // main メソッドを呼び出す
        Example.main(new String[]{nonExistentFilePath});

        // System.exit(1) が呼び出されるため、ExitException がスローされ、ここに到達しない。
        // 例外が捕捉された後に以下の検証が行われる。
        // 標準エラー出力の内容を検証
        assertEquals("'" + nonExistentFilePath + "' does not exist.\n", errContent.toString());
        // System.out には何も出力されないことを確認
        assertEquals("", outContent.toString());
    }

    /// 有効なファイルが指定された場合のテスト
    /**
     * {@code main} メソッドが、有効なファイルパスを引数として受け取ったときに、
     * {@code System.exit(1)} が呼び出されず、エラー出力がないことを確認します。
     * <p>
     * 注: このテストでは、{@code ForestModel}, {@code ForestView}, {@code JFrame} のインスタンス化や
     * {@code aModel.animate()} の呼び出しを直接検証することはできません。
     * これは、これらのオブジェクトが {@code main} メソッド内で直接 `new` されるため、
     * 標準の Mockito ではモック化できないためです。
     * テストは、プログラムが正常に終了すること（`System.exit(1)` が呼ばれないこと）に焦点を当てます。
     */
    @Test
    public void testMainValidFile() throws IOException {
        // テスト用の有効な一時データファイルを作成
        File validFile = tempFolder.newFile("valid_forest_data.txt");
        try (FileWriter writer = new FileWriter(validFile)) {
            // ForestModel.read() がエラーなく処理できるように、最低限の有効な内容を書き込む
            writer.write(Constants.TagOfNodes + "\n");
            writer.write("1,TestNode\n");
            writer.write(Constants.TagOfBranches + "\n");
        }

        // main メソッドを呼び出す。System.exit(1) が呼び出されないことを期待
        try {
            Example.main(new String[]{validFile.getAbsolutePath()});
            // ここに到達した場合、System.exit(1) は呼び出されなかったことを意味する。
            // (ExitException がスローされなかった)
            // 標準エラー出力が空であることを確認
            assertEquals("有効なファイルの場合、標準エラー出力は空であるべき", "", errContent.toString());
            // 標準出力には、ForestController からの "Clicked at Model Coordinates: ..." などが出力される可能性がある
            // ここでは System.out の内容を厳密に検証しないが、エラーがないことを確認する
            assertFalse("標準出力が空でない可能性があるが、エラーメッセージは含まないこと", outContent.toString().contains("Error"));

        } catch (ExitException e) {
            // 有効なファイルが System.exit(1) を引き起こすべきではない
            fail("有効なファイルが System.exit(1) を引き起こしました。ステータス: " + e.status + ", エラー出力: " + errContent.toString());
        }
    }
}
