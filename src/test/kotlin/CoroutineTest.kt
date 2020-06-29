import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class CoroutineTest {

    @Test
    fun `Coroutine Default`() {
        runBlocking {
            println("${Thread.activeCount()} threads active at the start")
            val time = measureTimeMillis {
                createCoroutines(100)
            }
            println("${Thread.activeCount()} threads active at end")
            println("Took $time ms")
        }
    }


    private suspend fun createCoroutines(amount: Int) {
        val jobs = ArrayList<Job>()
        for (i in 1..amount) {
            jobs += GlobalScope.launch {
                println("Started $i in ${Thread.currentThread().name}")
                delay(1000)
                println("Finished $i in ${Thread.currentThread().name}")
            }
        }
        jobs.forEach {
            it.join()
        }
    }
}