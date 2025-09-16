package nl.vanalphenict.utility

import kotlin.time.Duration
import kotlin.time.Instant

class TimeUtils {

    companion object {
        fun Instant.isOlderThan(currentTimestamp: Instant, duration: Duration) = this < currentTimestamp.minus(duration)
    }

}