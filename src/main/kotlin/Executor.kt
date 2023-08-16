import kotlinx.coroutines.*
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.toKotlinDuration

private val dispatcher = Executors.newCachedThreadPool { r ->
    val thread = Thread(r)
    thread.isDaemon = true
    thread
}.asCoroutineDispatcher()

private val scope = CoroutineScope(SupervisorJob())

interface ErrorListener {
    fun onError(reason: Throwable)
}

private fun buildContext(dispatcher: ExecutorCoroutineDispatcher, onError: ErrorListener?): CoroutineContext {
    return dispatcher + CoroutineExceptionHandler { _, e ->
        onError?.onError(e)
    }
}

fun <E> submitAsync(onError: ErrorListener, run: suspend () -> E) {
    val l = CountDownLatch(1)
    l.countDown()
    scope.launch(buildContext(dispatcher, onError)) {
        l.await()
        run()
    }
}

suspend fun <I, O> callSuspend(callee: suspend () -> I, transformer: (I) -> O) = transformer(callee())

suspend fun delay(d: Duration) = delay(d.toKotlinDuration())

