
import java.util.function.Supplier;

//条件分岐をメッセージで行うためのクラス
public class Condition {
    
    private Supplier<Boolean> condition = null;

    public Condition(Supplier<Boolean> conditionPassage) {
        this.condition = conditionPassage;
    }

    public void ifThenElse(Runnable thenPassage, Runnable elsePassage) {
        if(this.condition.get()) 
            thenPassage.run();
        else 
            elsePassage.run();
    }

    public void ifThen(Runnable thenPassage) {
        this.ifThenElse(thenPassage, () -> {;});
    }

    public void ifElse(Runnable elsePassage) {
        this.ifThenElse(() -> {;}, elsePassage);
    }

    public static void ifThenElse(Supplier<Boolean> aCondition, Runnable thenPassage, Runnable elsePassage) {
        new Condition(aCondition).ifThenElse(thenPassage, elsePassage);
    }

    public static void ifThen(Supplier<Boolean> aCondition, Runnable thenPassage) {
        Condition.ifThenElse(aCondition, thenPassage, () -> {;});
    }

    public static void ifElse(Supplier<Boolean> aCondition, Runnable elsePassage) {
        Condition.ifThenElse(aCondition, () -> {;}, elsePassage);
    }
}
