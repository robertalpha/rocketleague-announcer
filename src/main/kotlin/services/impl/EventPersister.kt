package nl.vanalphenict.services.impl

import kotlin.time.Clock
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.services.EventRepository

class EventPersister(val repository: EventRepository, val statRepository: StatRepository) : EventHandler {
    override fun handleStatMessage(
        msg: StatMessage
    ) {
        synchronized(this) {
            statRepository.addStatMessage(Clock.System.now(), msg)
            repository.addStatMessage(Clock.System.now(), msg)
        }
    }

    override fun handleGameEvent(
        msg: GameEventMessage
    ) {
        synchronized(this) {
            repository.addGameEventMessage(Clock.System.now(), msg)
        }
    }
}