package nl.vanalphenict.utility

import kotlin.time.Duration
import kotlin.time.Instant

class TimeUtils {

    companion object {
        fun Instant.bothHappenWithin(currentTimestamp: Instant, plusDuration: Duration) = currentTimestamp.minus(this).absoluteValue < plusDuration
    }

}