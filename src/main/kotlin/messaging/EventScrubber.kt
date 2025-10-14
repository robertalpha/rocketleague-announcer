package nl.vanalphenict.messaging

import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.LogMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.EventHandler

class EventScrubber(private val eventHandler: EventHandler) {

    private val messagesCache: MutableMap<Int, Instant> = HashMap()


    fun processGameEvent(msg: GameEventMessage) {
        messagesCache.computeIfAbsent(msg.hashCode()) {
            eventHandler.handleGameEvent(msg, RLAMetaData())
            Clock.System.now()
        }
        clearCache()
    }

    fun processStat(msg: StatMessage) {
        //Filter demolish stat message. Only use ticker
        if (msg.event == "Demolish" && msg.victim == null) return
        messagesCache.computeIfAbsent(msg.hashCode()) {
            eventHandler.handleStatMessage(msg, RLAMetaData())
            Clock.System.now()
        }
        //BallHit is a frequent stat message, never double
        if (msg.event != "BallHit") {
            clearCache()
        }
    }

    fun processGameTime(msg: GameTimeMessage) {
        messagesCache.computeIfAbsent(msg.hashCode()) {
            eventHandler.handleGameTime(msg)
            Clock.System.now()
        }
        clearCache()
    }

    fun processLog(msg: LogMessage) {
        eventHandler.handleLog(msg)
    }

    private fun clearCache() {
        messagesCache.entries.removeIf { it.value.plus (500.milliseconds) < Clock.System.now() }
    }
}

