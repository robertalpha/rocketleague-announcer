package nl.vanalphenict.utility

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

class TimeUtils {

    companion object {
        fun Instant.isOlderThan(duration: Duration) = this < Clock.System.now().minus(duration)
    }

}