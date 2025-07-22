package forest;

import java.security.Permission;

/**
 * System.exit() の呼び出しをインターセプトし、代わりに ExitException をスローする
 * カスタム SecurityManager です。
 * これにより、テスト中に System.exit() が呼び出されても JVM が終了するのを防ぎ、
 * テストの続行を可能にします。
 */
public class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
        // 他のすべてのパーミッションは許可します
    }

    @Override
    public void checkExit(int status) {
        // System.exit() が呼び出されたときに、ExitException をスローします
        super.checkExit(status); // 元の checkExit を呼び出して、適切なセキュリティチェックが行われることを保証
        throw new ExitException(status);
    }
}
