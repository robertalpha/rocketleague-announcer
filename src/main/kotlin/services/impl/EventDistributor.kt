package nl.vanalphenict.services.impl

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.JsonGameTimeMessage
import nl.vanalphenict.model.JsonLogMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.EventHandler

class EventDistributor(private val left: EventHandler, private val right: EventHandler) : EventHandler {
    override fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {
        left.handleStatMessage(msg, metaData)
        right.handleStatMessage(msg, metaData)
    }

    override fun handleGameEvent(msg: GameEventMessage, metaData: RLAMetaData) {
        left.handleGameEvent(msg, metaData)
        right.handleGameEvent(msg, metaData)
    }

    override fun handleGameTime(msg: JsonGameTimeMessage) {
        left.handleGameTime(msg)
        right.handleGameTime(msg)
    }

    override fun handleLog(msg: JsonLogMessage) {
        left.handleLog(msg)
        right.handleLog(msg)
    }
}