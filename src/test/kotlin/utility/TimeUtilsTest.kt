package nl.vanalphenict.utility

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.utility.TimeUtils.Companion.bothHappenWithin
import nl.vanalphenict.utility.TimeUtils.Companion.toGameString

class TimeUtilsTest {

    @Test
    fun testTimeUtils() {

        val t1 = Instant.parse("2020-01-01T12:00:00Z")
        val t2 = Instant.parse("2020-01-01T12:00:02Z")

        t2.bothHappenWithin(t1, 1.seconds) shouldBe false
        t2.bothHappenWithin(t1, 3.seconds) shouldBe true
    }

    @Test
    fun testDurationString() {
        1.seconds.toGameString shouldBe "0:01"
        61.seconds.toGameString shouldBe "1:01"
    }
}
