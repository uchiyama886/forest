<<<<<<<< HEAD:Forest_Program/src/main/java/utility/Loop.java
package utility;
========
package forest.utility;
>>>>>>>> 3b684d8c518047a0a5e077d6699cd35e4cf25860:Forest_Program/forest/utility/Loop.java

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
