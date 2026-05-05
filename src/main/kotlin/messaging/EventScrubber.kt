package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import nl.vanalphenict.model.JsonClockUpdatedSecondsData
import nl.vanalphenict.model.JsonLogMessage
import nl.vanalphenict.model.JsonMatchGuidData
import nl.vanalphenict.model.JsonStatfeedEventData
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.parseClockUpdatedSeconds
import nl.vanalphenict.model.parseGameEventMessage
import nl.vanalphenict.model.parseStatfeedEvent
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.services.GameTimeTrackerService
import nl.vanalphenict.utility.TimeService

class EventScrubber(
    private val eventHandler: EventHandler,
    private val gameTimeTrackerService: GameTimeTrackerService,
    private val timeService: TimeService,
) {

    private val log = KotlinLogging.logger {}

    private val messagesCache: MutableMap<Int, Instant> = HashMap()

    fun processGameEvent(eventName: String, msg: JsonMatchGuidData) {
        val key = (eventName + msg.matchGuid).hashCode()
        messagesCache.computeIfAbsent(key) {
            parseGameEventMessage(eventName, msg.matchGuid)?.let {
                val time = gameTimeTrackerService.getGameTime(msg.matchGuid)
                eventHandler.handleGameEvent(
                    it,
                    RLAMetaData(
                        matchGUID = it.matchGUID,
                        overtime = time.overtime,
                        remaining = time.remaining,
                    ),
                )
            } ?: log.warn { "Unable to parse game event message: $eventName $msg" }
            timeService.now()
        }
        clearCache()
    }

    fun processStatfeedEvent(msg: JsonStatfeedEventData) {
        // Filter demolish stat message without a secondary target
        if (StatEvents.DEMOLISH.eq(msg.eventName) && msg.secondaryTarget == null) return
        messagesCache.computeIfAbsent(msg.hashCode()) {
            parseStatfeedEvent(msg)?.let {
                val time = gameTimeTrackerService.getGameTime(msg.matchGuid)
                eventHandler.handleStatMessage(
                    it,
                    RLAMetaData(
                        matchGUID = it.matchGUID,
                        overtime = time.overtime,
                        remaining = time.remaining,
                    ),
                )
            } ?: log.warn { "Unable to parse statfeed event: $msg" }
            timeService.now()
        }
        clearCache()
    }

    fun processClockUpdatedSeconds(msg: JsonClockUpdatedSecondsData) {
        messagesCache.computeIfAbsent(msg.hashCode()) {
            val gameTimeMessage = parseClockUpdatedSeconds(msg)
            gameTimeTrackerService.storeGameTime(gameTimeMessage)
            eventHandler.handleGameTime(gameTimeMessage)
            timeService.now()
        }
        clearCache()
    }

    fun processLog(msg: JsonLogMessage) {
        eventHandler.handleLog(msg)
    }

    private fun clearCache() {
        messagesCache.entries.removeIf { it.value.plus(500.milliseconds) < timeService.now() }
    }
}
