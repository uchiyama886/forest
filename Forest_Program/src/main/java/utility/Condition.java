package utility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 条件分岐をメッセージで行うためのクラス
 */
public class Condition extends Object
{
    /**
    * 条件を供給者として保持するフィールドです。
    */
    private Supplier<Boolean> condition = null;

    /**
    * 条件分岐のコンストラクタです。
    * @param conditionPassage 条件を表すラムダ式
    */
    public Condition(Supplier<Boolean> conditionPassage)
    {
        this.condition = conditionPassage;
        return;
    }

    /**
    * 条件分岐を行います。
    * @param conditionPassage 自分の条件の評価が真のときに実行する条件ラムダ式
    * @return 論理積の真偽
    */
    public Boolean and(Supplier<Boolean> conditionPassage)
    {
        Boolean aBoolean = this.condition.get();
        if (aBoolean) { aBoolean = conditionPassage.get(); } else { ; }
        return aBoolean;
    }

    /**
    * 条件分岐を行います。
    * @param aCondition 条件を表すラムダ式
    * @param conditionPassage 上記の条件の評価が真のときに実行するラムダ式
    * @return 論理積の真偽
    */
    public static Boolean and(Supplier<Boolean> aCondition, Supplier<Boolean> conditionPassage)
    {
        Boolean aBoolean = new Condition(aCondition).and(conditionPassage);
        return aBoolean;
    }

    /**
     * 条件が真である時に指定されたアクションを実行するラムダ式
     * @param thenPassage 条件が真である時に実行するアクション
     */
    public void ifTrue(Runnable thenPassage)
    {
        if (this.condition.get())
        {
            thenPassage.run();
        }
    }
    
    /**
     * 条件が真である場合に指定されたアクションを実行するラムダ式
     * @param aCondition 条件を表すラムダ式
     * @param thenPassage 条件の評価が真のときに実行するアクション  
     */
    public static void ifTrue(Supplier<Boolean> aCondition, Runnable thenPassage)
    {
        new Condition(aCondition).ifTrue(thenPassage);
        return;
    }

    /**
    * 条件分岐を行います。
    * @param conditionPassage 自分の条件の評価が偽のときに実行する条件ラムダ式
    * @return 論理和の真偽
    */
    public Boolean or(Supplier<Boolean> conditionPassage)
    {
        Boolean aBoolean = this.condition.get();
        if (aBoolean) { ; } else { aBoolean = conditionPassage.get(); }
        return aBoolean;
    }

    /**
    * 条件分岐を行います。
    * @param aCondition 条件を表すラムダ式
    * @param conditionPassage 上記の条件の評価が偽のときに実行するラムダ式
    * @return 論理和の真偽
    */
    public static Boolean or(Supplier<Boolean> aCondition, Supplier<Boolean> conditionPassage)
    {
        Boolean aBoolean = new Condition(aCondition).or(conditionPassage);
        return aBoolean;
    }

    /**
     * 条件が偽である場合に指定されたアクションを実行する
     * @param elsePassage 条件の評価が偽のときに実行するアクション
     */
    public void ifFalse(Runnable elsePassage)
    {
        if (!this.condition.get()) { // 条件が偽の場合
            elsePassage.run();
        }
        return;
    }

    /**
     * 条件が偽である場合に指定されたアクションを実行する
     * @param aCondition 条件を表すラムダ式
     * @param elsePassage 条件の評価が偽のときに実行するアクション
     */
    public static void ifFalse(Supplier<Boolean> aCondition, Runnable elsePassage)
    {
        new Condition(aCondition).ifFalse(elsePassage);
        return;
    }


    /**
    * 条件分岐を行います。
    * @param elsePassage 自分の条件の評価が偽のときに実行するラムダ式
    */
    public void ifElse(Runnable elsePassage)
    {
        this.ifThenElse(() -> { ; }, elsePassage);
        return;
    }

    /**
    * 条件分岐を行います。
    * @param aCondition 条件を表すラムダ式
    * @param elsePassage 上記の条件の評価が偽のときに実行するラムダ式
    */
    public static void ifElse(Supplier<Boolean> aCondition, Runnable elsePassage)
    {
        Condition.ifThenElse(aCondition, () -> { ; }, elsePassage);
        return;
    }

    /**
    * 条件分岐を行います。
    * @param thenPassage 自分の条件の評価が真のときに実行するラムダ式
    */
    public void ifThen(Runnable thenPassage)
    {
        this.ifThenElse(thenPassage, () -> { ; });
        return;
    }

    /**
    * 条件分岐を行います。
    * @param aCondition 条件を表すラムダ式
    * @param thenPassage 上記の条件の評価が真のときに実行するラムダ式
    */
    public static void ifThen(Supplier<Boolean> aCondition, Runnable thenPassage)
    {
        Condition.ifThenElse(aCondition, thenPassage, () -> { ; });
        return;
    }
    
    /**
    * 条件分岐を行います。
    * @param thenPassage 自分の条件の評価が真のときに実行するラムダ式
    * @param elsePassage 自分の条件の評価が偽のときに実行するラムダ式
    */
    public void ifThenElse(Runnable thenPassage, Runnable elsePassage)
    {
        if (this.condition.get()) { thenPassage.run(); } else { elsePassage.run(); }
        return;
    }

    /**
    * 条件分岐を行います。
    * @param aCondition 条件を表すラムダ式
    * @param thenPassage 上記の条件の評価が真のときに実行するラムダ式
    * @param elsePassage 上記の条件の評価が偽のときに実行するラムダ式
    */
    public static void ifThenElse(Supplier<Boolean> aCondition, Runnable thenPassage, Runnable elsePassage)
    {
        new Condition(aCondition).ifThenElse(thenPassage, elsePassage);
        return;
    }

    /**
     * 条件が真である限り指定されたアクションを実行する
     * ループの各イテレーションで条件を再評価する
     * @param loopBody 各イテレーションで実行されるループ本体のアクション
     */
    public void whileTrue(Runnable loopBody)
    {
        while (this.condition.get()) {
            try {
                loopBody.run(); // ループ本体の処理を実行
            } catch (RuntimeException e) {
                throw e; // RuntimeExceptionをスロー
            }
            
        }
    }

    /**
     * Case文の選択肢を表す内部クラス
     * Case文の追加、Default Case、
     */
    public static class Case {
        private Supplier<Boolean> condition;
        private Runnable action;

        public Case(final Supplier<Boolean> condition, final Runnable action) {
            this.condition = condition;
            this.action = action;
        }

        /**
         * このCaseの条件が真かどうかを評価します。
         * @return 条件が真の場合true
         */
        public boolean evaluate() {
            return condition.get();
        }

        /**
         * このCaseに関連付けられたアクションを実行します。
         */
        public void execute() {
            action.run();
        }
    }

    /**
     * 複数の条件とそれに対応するアクションを処理するためのクラスです。
     */
    public static class Switch {
        private List<Case> cases;
        private Runnable defaultAction;
        private boolean executed; // 最初のマッチしたケースが実行されたかを追跡する

        public Switch() {
            this.cases = new ArrayList<>();
            this.defaultAction = () -> {};
            this.executed = false;
        }

        /**
         * 新しいCaseを追加する。
         * @param condition このケースの条件
         * @param action このケースが選択されたときに実行するアクション
         * @return このSwitchインスタンス
         */
        public Switch addCase(Supplier<Boolean> condition, Runnable action) {
            this.cases.add(new Case(condition, action));
            return this;
        }

        /**
         * デフォルトのアクションを設定する。どのケースもマッチしなかった場合に実行される。
         * @param action デフォルトのアクション
         * @return このSwitchインスタンス
         */
        public Switch defaultCase(Runnable action) {
            this.defaultAction = action;
            return this;
        }

        /**
         * ケースを評価し、最初に見つかった真の条件に対応するアクションを実行します。
         * どのケースもマッチしない場合は、デフォルトアクションを実行します。
         */
        public void evaluate() {
            executed = false; // 評価前にリセット
            for (Case singleCase : cases) {
                if (singleCase.evaluate()) {
                    singleCase.execute();
                    executed = true; // 実行されたことをマーク
                    break; // 最初のマッチしたケースで終了
                }
            }
            // どのケースも実行されなかった場合、デフォルトアクションを実行
            if (!executed) {
                defaultAction.run();
            }
        }
    }
    
}