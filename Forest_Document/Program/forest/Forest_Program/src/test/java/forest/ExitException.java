package forest;

/**
 * System.exit() が呼び出されたときにスローされるカスタム例外です。
 * これにより、テスト中に JVM が終了するのを防ぎます。
 */
public class ExitException extends SecurityException {
    public final int status; // System.exit() に渡されたステータスコード

    public ExitException(int status) {
        this.status = status;
    }
}
