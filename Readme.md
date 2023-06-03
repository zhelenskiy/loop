# Infinite `loop` function with `break` and `continue`

There are two options of the loop function:
* ```kotlin
  fun loop(body: suspend LoopScope<Unit>.() -> Unit): Unit
  ```
* ```kotlin
  fun <T> loopWithResult(@BuilderInference body: suspend LoopScope<T>.() -> Unit): T
  ```
The second one supports parameterized `break` function whose parameter is returned from `loopWithResult`.

`continue` and `break` analogues are defined inside `LoopScope` interface:
```kotlin
@RestrictsSuspension
interface LoopScope<T> {
    suspend fun breakLoop(result: T): Nothing
    suspend fun continueLoop(): Nothing
}

suspend fun LoopScope<Unit>.breakLoop(): Nothing = breakLoop(Unit)
```

The solution does not depend on the exceptions, so it is safe to wrap `break`s and `continue`s with `runCatching` and `try`.
The solution is also not recursive, so number of iterations is not limited by the stack size.

## Example
```kotlin
var cur = 0
loop {
    cur++
    
    if (cur == 2) continueLoop()
    if (cur == 5) breakLoop()
    println(cur)
}
```

Output:
```text
1
3
4
```
