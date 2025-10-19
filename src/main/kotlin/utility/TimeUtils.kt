package nl.vanalphenict.utility

import kotlin.time.Duration
import kotlin.time.Instant

class TimeUtils {

    companion object {
        fun Instant.bothHappenWithin(currentTimestamp: Instant, plusDuration: Duration) =
            currentTimestamp.minus(this).absoluteValue < plusDuration

        inline val Duration.toGameString: String
            get() = toComponents { _, _, m, s, _ -> String.format(String.format("%d:%02d", m, s)) }
    }
}
