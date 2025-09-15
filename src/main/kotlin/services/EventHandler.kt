package nl.vanalphenict.services

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.LogMessage
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.impl.EventDistributor

interface EventHandler {
    fun handleStatMessage(msg: StatMessage) {}
    fun handleGameEvent(msg: GameEventMessage) {}
    fun handleGameTime(msg: GameTimeMessage) {}
    fun handleLog(msg: LogMessage) {}

    class Builder(private var handler: EventHandler) {

        fun add(handler: EventHandler): Builder {
            this.handler = EventDistributor(this.handler, handler)
            return this
        }

        fun build(): EventHandler {
            return this.handler
        }
    }
}