package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import nl.vanalphenict.model.JsonGameEventMessage
import nl.vanalphenict.model.JsonGameTimeMessage
import nl.vanalphenict.model.JsonLogMessage
import nl.vanalphenict.model.JsonStatMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.parseGameEventMessage
import nl.vanalphenict.model.parseStatMessage
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.utility.TimeService

class EventScrubber(private val eventHandler: EventHandler, private val timeService: TimeService) {

    private val log = KotlinLogging.logger { }

    private val messagesCache: MutableMap<Int, Instant> = HashMap()

    fun processGameEvent(msg: JsonGameEventMessage) {
        messagesCache.computeIfAbsent(msg.hashCode()) {
            parseGameEventMessage(msg)?.let {
                eventHandler.handleGameEvent(it, RLAMetaData())
            } ?: log.warn { "Unable to parse game event message: $msg" }
            timeService.now()
        }
        clearCache()
    }

    fun processStat(msg: JsonStatMessage) {
        //Filter demolish stat message. Only use ticker
        if (StatEvents.DEMOLISH.eq(msg.event) && msg.victim == null) return
        messagesCache.computeIfAbsent(msg.hashCode()) {
            parseStatMessage(msg)?.let {
                eventHandler.handleStatMessage(it, RLAMetaData())
            } ?: log.warn { "Unable to parse stat message: $msg" }
            timeService.now()
        }
        //BallHit is a frequent stat message, never double
        if (msg.event != "BallHit") {
            clearCache()
        }
    }

    fun processGameTime(msg: JsonGameTimeMessage) {
        messagesCache.computeIfAbsent(msg.hashCode()) {
            eventHandler.handleGameTime(msg)
            timeService.now()
        }
        clearCache()
    }

    fun processLog(msg: JsonLogMessage) {
        eventHandler.handleLog(msg)
    }

    private fun clearCache() {
        messagesCache.entries.removeIf { it.value.plus (500.milliseconds) < timeService.now() }
    }
}
