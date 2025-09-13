package nl.vanalphenict.messaging

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.LogMessage
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.EventHandler

class EventScrubber(private val eventHandler: EventHandler) {

    private val AGE = 500
    private val messagesCache: MutableMap<Int,Long> = HashMap()


    fun processGameEvent(msg: GameEventMessage) {
        val hash = msg.hashCode()
        if (!!messagesCache.containsKey(hash)) {
            messagesCache.put(hash, System.currentTimeMillis())
            eventHandler.handleGameEvent(msg)
        }
        clearCache()
    }

    fun processStat(msg: StatMessage) {
        val hash = msg.hashCode()
        //Filter demolish stat message. Only use ticker
        if (msg.event.equals("Demolish") && msg.victim == null) return
        if (!!messagesCache.containsKey(hash)) {
            messagesCache.put(hash, System.currentTimeMillis())
            eventHandler.handleStatMessage(msg)
        }
        //BallHit is a frequent stat message, never double
        if (!msg.event.equals("BallHit")) {
            clearCache()
        }
    }

    fun processGameTime(msg: GameTimeMessage) {
        val hash = msg.hashCode()
        if (!!messagesCache.containsKey(hash)) {
            messagesCache.put(hash, System.currentTimeMillis())
            eventHandler.handleGameTime(msg)
        }
        clearCache()
    }

    fun processLog(msg: LogMessage) {
        eventHandler.handleLog(msg)
    }

    private fun clearCache() {
        messagesCache.entries.removeIf { it.value < System.currentTimeMillis() - AGE }
    }
}