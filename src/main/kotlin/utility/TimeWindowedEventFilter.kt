package nl.vanalphenict.utility

import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Filters events based on a time window.
 *
 * @param equivalenceMethod A function that returns a identifying value for an event. Equivalent
 *   events should produce the same value.
 * @param action The action to perform when the event is seen for the first time.
 * @param timeWindow The duration of the time window within equivalent events are considered.
 * @param timeService The time service to use for the current time.
 */
class TimeWindowedEventFilter<T, H>(
    val equivalenceMethod: (T) -> H,
    val action: (T) -> Unit,
    val timeWindow: Duration,
    val timeService: TimeService,
) {

    private val eventHashes = mutableMapOf<H, Instant>()

    /**
     * Process a new event. If the event has not been seen in the time window, the action will be
     * performed.
     */
    fun process(event: T) {
        val key = equivalenceMethod(event)
        purgeOldEvents()
        eventHashes.computeIfAbsent(
            key,
            {
                action(event)
                timeService.now()
            },
        )
    }

    private fun purgeOldEvents() {
        eventHashes.entries.removeIf { it.value.plus(timeWindow) < timeService.now() }
    }
}
