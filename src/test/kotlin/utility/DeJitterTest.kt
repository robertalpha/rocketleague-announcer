package nl.vanalphenict.utility

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class DeJitterTest {

    fun compare (l1:Long, l2:Long): Int {
        return l1.compareTo(l2)
    }

    @Test
    fun testDeJitter() {
        val answers = ArrayList<Long>()
        val deJittered = DeJitter<Long>(
            100.milliseconds,
             this::compare,
            { o -> answers.add(o) }
        )

        true shouldBe true

        deJittered.add(1L) shouldBe true
        Thread.sleep(10)
        deJittered.add(2L) shouldBe true
        Thread.sleep(10)
        deJittered.add(null) shouldBe false
        Thread.sleep(10)
        deJittered.add(2L) shouldBe false
        Thread.sleep(10)
        deJittered.add(3L) shouldBe true
        Thread.sleep(10)
        deJittered.add(2L) shouldBe false
        Thread.sleep(101)
        deJittered.add(1L) shouldBe true
        Thread.sleep(101)

        answers shouldContainExactly listOf(3,1)

    }


}