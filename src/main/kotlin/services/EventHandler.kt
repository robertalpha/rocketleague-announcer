package nl.vanalphenict.services

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.JsonGameTimeMessage
import nl.vanalphenict.model.JsonLogMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.impl.EventDistributor

interface EventHandler {
    fun handleStatMessage(
        msg: StatMessage,
        metaData: RLAMetaData) {}
    fun handleGameEvent(
        msg: GameEventMessage,
        metaData: RLAMetaData) {}
    fun handleGameTime(msg: JsonGameTimeMessage) {}
    fun handleLog(msg: JsonLogMessage) {}

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