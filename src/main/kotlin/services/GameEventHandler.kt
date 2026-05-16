package nl.vanalphenict.services

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.impl.EventDistributor

interface GameEventHandler {
    /**
     * TODO: Refactor this method.
     *
     * Currently it depends on the gameStateRepository already having been updated to the latest
     * state so all dependent services should use that for their data. This however is a different
     * approaches than all the other methods in this class in which the data is supplied by
     * parameters
     */
    fun handleTick(matchGuid: String)

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
