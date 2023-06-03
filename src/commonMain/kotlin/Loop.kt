import kotlin.coroutines.*
import kotlin.experimental.ExperimentalTypeInference


@RestrictsSuspension
interface LoopScope<T> {
    suspend fun breakLoop(result: T): Nothing
    suspend fun continueLoop(): Nothing
}

suspend fun LoopScope<Unit>.breakLoop(): Nothing = breakLoop(Unit)

private typealias ScopeFunction<T> = suspend LoopScope<T>.() -> Unit

fun loop(body: ScopeFunction<Unit>): Unit = loopWithResult(body)


@OptIn(ExperimentalTypeInference::class)
fun <T> loopWithResult(@BuilderInference body: suspend LoopScope<T>.() -> Unit): T {
    val loopException = Exception()
    var result: Result<T> = Result.failure(loopException)
    suspend fun f() = loopIteration(body = body, onSuccess = { result = Result.success(it) })
    do {
        (::f).startCoroutine(Continuation(EmptyCoroutineContext) {})
    } while (result.exceptionOrNull() == loopException)
    return result.getOrThrow()
}

private suspend fun <T> loopIteration(onSuccess: (T) -> Unit, body: ScopeFunction<T>) {
    body(object : LoopScope<T> {
        override suspend fun breakLoop(result: T): Nothing {
            onSuccess(result)
            finishCoroutine()
        }

        override suspend fun continueLoop(): Nothing = finishCoroutine()
    })
}

private suspend fun finishCoroutine(): Nothing = suspendCoroutine {  }
