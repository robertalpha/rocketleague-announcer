package nl.vanalphenict.services.impl

import nl.vanalphenict.services.EventRepository
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.EventHandler
import java.time.ZonedDateTime

class EventPersister(val repository: EventRepository, val statRepository: StatRepository) : EventHandler {
    override fun handleStatMessage(
        msg: StatMessage
    ) {
        synchronized(this) {
            statRepository.addStatMessage(System.currentTimeMillis(), msg)
            repository.addStatMessage(ZonedDateTime.now(), msg)
        }
    }

    override fun handleGameEvent(
        msg: GameEventMessage
    ) {
        synchronized(this) {
            repository.addGameEventMessage(ZonedDateTime.now(), msg)
        }
    }
}