import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class TestBug {
    @Test
    void test() {
        ExecutorKt.submitAsync(Throwable::printStackTrace, (Function1<Continuation<? super Unit>, Object>) noContinuation -> {
            ExecutorKt.callSuspend(afterDelay -> {
                ExecutorKt.delay(Duration.ofSeconds(3), afterDelay);
                return null;
            }, (Function1<Unit, Unit>) delayRes -> {
                System.out.println("delay end");
                return Unit.INSTANCE;
            }, noContinuation);
            return null;
        });
        while (true) ;
    }
}
