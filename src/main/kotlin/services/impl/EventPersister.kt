package nl.vanalphenict.services.impl

import kotlin.time.Clock
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.EventHandler

class EventPersister(
    val statRepository: StatRepository,
    val gameEventRepository: GameEventRepository) : EventHandler {

    override fun handleStatMessage(
        msg: StatMessage,
        metaData: RLAMetaData
    ) {
        synchronized(this) {
            statRepository.addStatMessage(Clock.System.now(), msg)
        }
    }

    override fun handleGameEvent(
        msg: GameEventMessage,
        metaData: RLAMetaData
    ) {
        synchronized(this) {
            gameEventRepository.addGameEventMessage(Clock.System.now(), msg)
        }
    }
}