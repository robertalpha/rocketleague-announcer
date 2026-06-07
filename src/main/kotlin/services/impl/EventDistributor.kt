package nl.vanalphenict.services.impl

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.GameEventHandler

class EventDistributor(private val left: GameEventHandler, private val right: GameEventHandler) :
    GameEventHandler {

    override fun handleTick(matchGuid: String) {
        left.handleTick(matchGuid)
        right.handleTick(matchGuid)
    }

    override fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {
        left.handleStatMessage(msg, metaData)
        right.handleStatMessage(msg, metaData)
    }

    override fun handleGameEvent(msg: GameEventMessage, metaData: RLAMetaData) {
        left.handleGameEvent(msg, metaData)
        right.handleGameEvent(msg, metaData)
    }

    override fun handleGameTime(msg: GameTimeMessage) {
        left.handleGameTime(msg)
        right.handleGameTime(msg)
    }
}
