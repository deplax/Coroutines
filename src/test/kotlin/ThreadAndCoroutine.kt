import kotlinx.coroutines.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.concurrent.Executors
import kotlin.math.pow
import kotlin.system.measureTimeMillis

@ObsoleteCoroutinesApi
class ThreadAndCoroutine {
    // -Dkotlinx.coroutines.debug

    private val threadCount = 5
    private val epochPi = 2_000_000
    private val context = newFixedThreadPoolContext(threadCount, "whale_thread")
    private val logger = LoggerFactory.getLogger(javaClass)

    @Test
    fun timeTest() {
        val amount = 10

        val coroutineTime = runBlocking { measureTimeMillis { runCoroutine(amount) } }
        val threadTime = measureTimeMillis { runThread(amount) }

        logger.info("coroutine $coroutineTime ms")
        logger.info("thread $threadTime ms")
    }


    private suspend fun runCoroutine(amount: Int) {
        val jobs = ArrayList<Job>()
        for (i in 1..amount) {
            jobs += GlobalScope.launch(context) {
                logger.info("Started $i in ${Thread.currentThread().name}")
                logger.info(getPi(epochPi).toString())
            }
        }
        jobs.forEach {
            it.join()
        }
    }


    private fun runThread(amount: Int) {
        val executor = Executors.newWorkStealingPool(threadCount)
        (1..amount).map { i ->
            executor.submit(run(i))
        }.map {
            logger.info(it.get().toString())
        }
    }

    private fun run(i: Int): () -> BigDecimal {
        return {
            logger.info("$i")
            getPi(epochPi)
        }
    }

    private fun getPi(count: Int): BigDecimal {
        var sum = BigDecimal(0)
        for (i in 1..count) {
            val denominator = (-1.0).pow(i + 1)
            val numerator = 2 * i - 1
            sum += BigDecimal(denominator / numerator)
        }
        return sum * BigDecimal(4)
    }

    @Test
    fun `이거 왜 리턴 없어??`() {
        val result = Executors.newSingleThreadExecutor().submit {10}.get()
        assertThat(result).isNull()
    }
}
