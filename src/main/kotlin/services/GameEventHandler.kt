package nl.vanalphenict.services

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.impl.EventDistributor

interface GameEventHandler {
    fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {}

    fun handleGameEvent(msg: GameEventMessage, metaData: RLAMetaData) {}

    fun handleGameTime(msg: GameTimeMessage) {}

    class Builder(private var handler: GameEventHandler) {

        fun add(handler: GameEventHandler): Builder {
            this.handler = EventDistributor(this.handler, handler)
            return this
        }

        fun build(): GameEventHandler {
            return this.handler
        }
    }
}
