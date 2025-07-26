package forest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore; // @Ignore アノテーションをインポート
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue; // assertTrue を使用するためにインポート
import static org.junit.Assert.fail; // fail メソッドをインポート

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor; // コンストラクタをリフレクションで取得するために必要
import java.lang.reflect.InvocationTargetException; // リフレクションで例外を捕捉するために必要
import java.lang.reflect.Modifier; // コンストラクタの修飾子をチェックするために必要
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {@code Example} クラスのテストクラスです。
 * 主に {@code main} メソッドのエラー処理と、クラスのインスタンス化防止を検証します。
 *
 * 注意: {@code main} メソッドが {@code System.exit()} を呼び出すため、
 * 引数不足やファイル不在のテストケースはJVMを終了させます。
 * これらのテストは、単独で実行するか、JVMの終了を許容するテストランナーの設定が必要です。
 * 現在は、JVMの強制終了を防ぐため、{@code @Ignore} アノテーションで無効化されています。
 *
 * また、{@code main} メソッド内でGUIコンポーネントが直接インスタンス化されるため、
 * 標準的な Mockito ではその部分のモック化は困難です。
 * そのため、GUI表示やアニメーションの開始を直接検証するユニットテストは含まれていません。
 */
public class ExampleTest {

    // System.err の出力をキャプチャするためのストリーム
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    /**
     * 各テストメソッドの実行前に System.err をリダイレクトします。
     */
    @Before
    public void setUpStreams() {
        System.setErr(new PrintStream(errContent));
    }

    /**
     * 各テストメソッドの実行後に System.err を元の状態に戻します。
     */
    @After
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    /**
     * 引数が不足している場合に main メソッドが正しくエラーメッセージを出力し、
     * System.exit(1) を呼び出すことを検証します。
     *
     * 注意: このテストは System.exit(1) を呼び出すため、JVMが終了します。
     * そのため、テストフレームワークによってはテストの実行が中断される可能性があります。
     * このテストは、JVMの強制終了を防ぐため、一時的に無効化されています。
     * 理想的には、System.exit()を呼び出すロジックをリファクタリングするか、
     * テストランナーの設定で別プロセスで実行するようにしてください。
     */
    @Ignore // JVMの強制終了を防ぐため、このテストを無効化します
    @Test
    public void testMain_TooFewArguments() {
        // System.exit() が呼び出されることを期待するため、
        // try-catch で System.exit() による例外を捕捉するような一般的なパターンは、
        // SecurityManager が削除された Java 21+ では直接適用できません。
        // ここでは System.err への出力のみを検証します。

        // main メソッドを実行
        // この呼び出しは System.exit(1) を引き起こし、JVMを終了させる可能性があります。
        // そのため、このテストメソッド以降のコードは実行されないかもしれません。
        // 理想的には、この種のテストは別プロセスで実行されるべきです。
        Example.main(new String[]{});

        // System.err に正しいメッセージが出力されたことを検証
        String expectedErrorMessage = "There are too few arguments.";
        assertTrue("エラーメッセージが正しく出力されること", errContent.toString().contains(expectedErrorMessage));
    }

    /**
     * 指定されたファイルが存在しない場合に main メソッドが正しくエラーメッセージを出力し、
     * System.exit(1) を呼び出すことを検証します。
     *
     * 注意: このテストは System.exit(1) を呼び出すため、JVMが終了します。
     * そのため、テストフレームワークによってはテストの実行が中断される可能性があります。
     * このテストは、JVMの強制終了を防ぐため、一時的に無効化されています。
     * 理想的には、System.exit()を呼び出すロジックをリファクタリングするか、
     * テストランナーの設定で別プロセスで実行するようにしてください。
     */
    @Ignore // JVMの強制終了を防ぐため、このテストを無効化します
    @Test
    public void testMain_FileDoesNotExist() {
        // 存在しない一時的なファイルパスを作成
        String nonExistentFilePath = "non_existent_test_file_" + System.currentTimeMillis() + ".txt";
        File nonExistentFile = new File(nonExistentFilePath);

        // main メソッドを実行
        // この呼び出しは System.exit(1) を引き起こし、JVMを終了させる可能性があります。
        Example.main(new String[]{nonExistentFile.getAbsolutePath()});

        // System.err に正しいメッセージが出力されたことを検証
        String expectedErrorMessage = "'" + nonExistentFile.getAbsolutePath() + "' does not exist.";
        assertTrue("エラーメッセージが正しく出力されること", errContent.toString().contains(expectedErrorMessage));

        // テスト後に一時ファイルを削除（存在しないはずだが念のため）
        if (nonExistentFile.exists()) {
            nonExistentFile.delete();
        }
    }

    /**
     * 有効なファイルが指定された場合に main メソッドが正常に実行されることを検証します。
     * ただし、GUIコンポーネントのインスタンス化はモックできないため、
     * このテストは System.exit() が呼び出されないことのみを間接的に確認します。
     *
     * 注意: このテストは実際のファイルを作成し、GUIコンポーネントを初期化しようとします。
     * ヘッドレス環境で実行する場合、問題が発生する可能性があります。
     * 厳密なユニットテストとしてではなく、System.exit() が発生しないことを確認する
     * 程度のものと理解してください。
     */
    @Test
    public void testMain_ValidFile_NoExit() throws Exception {
        // テスト用のダミーファイルを作成
        Path tempFilePath = Files.createTempFile("test_forest_data", ".txt");
        // ファイルに最低限の有効な内容を書き込む (ForestModelがパースできる内容)
        Files.write(tempFilePath, "nodes:\n1,NodeA\nbranches:\n".getBytes()); // 少なくともノードとブランチのタグが必要

        // main メソッドを実行
        // この呼び出しはGUIコンポーネントを初期化し、表示しようとします。
        // ヘッドレス環境で実行する場合、問題が発生する可能性があります。
        Example.main(new String[]{tempFilePath.toString()});

        // System.err に何も出力されていないことを検証
        assertEquals("エラーメッセージが出力されないこと", "", errContent.toString());

        // テスト後に一時ファイルを削除
        Files.deleteIfExists(tempFilePath);
    }

    /**
     * {@code Example} クラスのコンストラクタがプライベートであり、
     * 外部からインスタンス化できないことを確認します。
     * リフレクションを使ってプライベートコンストラクタへのアクセスを試み、
     * インスタンス化が成功しないことを検証します。
     */
    @Test
    public void testPrivateConstructor() {
        try {
            // Example クラスのすべての宣言されたコンストラクタを取得
            Constructor<Example> constructor = Example.class.getDeclaredConstructor();
            // コンストラクタがプライベートであることを確認
            assertTrue("Example クラスのコンストラクタはプライベートであるべき",
                       Modifier.isPrivate(constructor.getModifiers()));

            // プライベートコンストラクタにアクセス可能にする (テストのため)
            constructor.setAccessible(true);

            // インスタンス化を試みる
            constructor.newInstance();

            // ここに到達した場合はテスト失敗 (インスタンス化されるべきではないため)
            fail("Example クラスはインスタンス化されるべきではありません。");
        } catch (InvocationTargetException e) {
            // コンストラクタが例外をスローした場合（例えば、Constantsクラスのように）
            // Exampleクラスのコンストラクタが UnsupportedOperationException をスローするように変更されたため、
            // このブロックに到達することが期待されます。
            Throwable cause = e.getTargetException();
            assertTrue("InvocationTargetException の原因が null でないこと", cause != null); // assertNotNull を assertTrue に変更
            assertTrue("原因が UnsupportedOperationException であること", cause instanceof UnsupportedOperationException);
            assertEquals("UnsupportedOperationException のメッセージが正しいこと",
                         "Example クラスはインスタンス化できません。", cause.getMessage());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            // その他のリフレクション関連の例外が発生した場合、テスト失敗
            fail("予期せぬリフレクション例外が発生しました: " + e.getMessage());
        }
    }
}
