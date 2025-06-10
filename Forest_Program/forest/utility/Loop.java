package forest.utility;

import java.util.function.Supplier;

public class Loop {

    private Supplier<Boolean> condition = null;

    public Loop(Supplier<Boolean> conditionPassage) {
        this.condition = conditionPassage;
    }

    public void whileTrue(Runnable body) {
        while(this.condition.get()) {
            body.run();
        }
    }

    public static void whileTrue(Supplier<Boolean> aCondition, Runnable body) {
        while(aCondition.get()) {
            body.run();
        }
    }
}
