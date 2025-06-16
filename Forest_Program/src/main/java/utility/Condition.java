<<<<<<<< HEAD:Forest_Program/src/main/java/utility/Condition.java
package utility;
========
package forest.utility;
>>>>>>>> 3b684d8c518047a0a5e077d6699cd35e4cf25860:Forest_Program/forest/utility/Condition.java

import java.util.function.Supplier;

//条件分岐をメッセージで行うためのクラス
public class Condition {
    
    private Supplier<Boolean> condition = null;

    public Condition(Supplier<Boolean> supplier) {
        this.condition = supplier;
    }

    /** 
    * @param thenPassage trueなら実行されるラムダ式
    * @param elsePassage falseなら実行されるラムダ式
    */
    public void ifThenElse(Runnable thenPassage, Runnable elsePassage) {
        if(this.condition.get()) 
            thenPassage.run();
        else 
            elsePassage.run();
    }

    public void ifTrue(Runnable thenPassage) {
        this.ifThenElse(thenPassage, () -> {;});
    }

    public void ifElse(Runnable elsePassage) {
        this.ifThenElse(() -> {;}, elsePassage);
    }

    public static void ifThenElse(Supplier<Boolean> aCondition, Runnable thenPassage, Runnable elsePassage) {
        new Condition(aCondition).ifThenElse(thenPassage, elsePassage);
    }

    public static void ifTrue(Supplier<Boolean> aCondition, Runnable thenPassage) {
        Condition.ifThenElse(aCondition, thenPassage, () -> {;});
    }

    public static void ifElse(Supplier<Boolean> aCondition, Runnable elsePassage) {
        Condition.ifThenElse(aCondition, () -> {;}, elsePassage);
    }
}
