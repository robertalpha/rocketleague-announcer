package nl.vanalphenict.services.impl

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.utility.TimeService

class EventPersister(
    val statRepository: StatRepository,
    val gameEventRepository: GameEventRepository,
    val timeService: TimeService,
) : EventHandler {

    override fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {
        synchronized(this) { statRepository.addStatMessage(timeService.now(), msg) }
    }

    override fun handleGameEvent(msg: GameEventMessage, metaData: RLAMetaData) {
        synchronized(this) { gameEventRepository.addGameEventMessage(timeService.now(), msg) }
    }
}
