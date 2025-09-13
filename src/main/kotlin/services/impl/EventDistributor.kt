package nl.vanalphenict.services.impl

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.LogMessage
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.EventHandler

class EventDistributor(private val left: EventHandler, private val right: EventHandler) : EventHandler {
    override fun handleStatMessage(msg: StatMessage) {
        left.handleStatMessage(msg)
        right.handleStatMessage(msg)
    }

    override fun handleGameEvent(msg: GameEventMessage) {
        left.handleGameEvent(msg)
        right.handleGameEvent(msg)
    }

    override fun handleGameTime(msg: GameTimeMessage) {
        left.handleGameTime(msg)
        right.handleGameTime(msg)
    }

    override fun handleLog(msg: LogMessage) {
        left.handleLog(msg)
        right.handleLog(msg)
    }
}