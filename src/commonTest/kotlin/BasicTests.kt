import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BasicTests {
    @Test
    fun continueAndBreak() {
        val numbers = buildList {
            var cur = 0
            loop {
                cur++
                
                if (cur == 2) continueLoop()
                if (cur == 5) breakLoop()
                add(cur)
            }
        }
        assertContentEquals(listOf(1, 3, 4), numbers)
    }
    
    @Test
    fun noStackOverflow() {
        var current = 1L
        var sum = 0L
        loop { 
            if (current > 1_000_000) breakLoop()
            sum += current
            current++
        }
        assertEquals((1L..1_000_000L).sum(), sum)
    }
    
    @Test
    fun breakWithType() {
        val result = loopWithResult { breakLoop(100) }
        assertEquals(100, result)
    }
    
    @Test
    fun breakInsideTry() {
        var x = false
        loop {
            runCatching {
                x = true
                this@loop.breakLoop()
            }
        }
        assertTrue(x)
    }
}