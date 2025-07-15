package utility;

import java.util.function.Supplier;

/**
 * ループをメッセージ式で行うためのクラス
 */
public class Loop {

    /**
     * ループの実行を制御する条件を供給するSupplier。
     */
    private Supplier<Boolean> condition = null;

    /**
     * Loopクラスの新しいインスタンスを生成します。
     * @param conditionPassage ループが続行されるべきか（true）または停止されるべきか（false）を決定する論理条件を提供するSupplier。
     */
    public Loop(Supplier<Boolean> conditionPassage) {
        this.condition = conditionPassage;
    }

    /**
     * コンストラクタで指定された条件が真である限り、指定されたコードブロックを繰り返し実行します。
     * @param body 条件が真である限り実行されるRunnable（コードブロック）。
     */
    public void whileTrue(Runnable body) {
        while(this.condition.get()) {
            body.run();
        }
    }

    /**
     * 指定された条件が真である限り、指定されたコードブロックを繰り返し実行する静的ユーティリティメソッドです。
     * @param aCondition ループが続行されるべきか（true）または停止されるべきか（false）を決定する論理条件を提供するSupplier。
     * @param body 条件が真である限り実行されるRunnable（コードブロック）。
     */
    public static void whileTrue(Supplier<Boolean> aCondition, Runnable body) {
        while(aCondition.get()) {
            body.run();
        }
    }
}
