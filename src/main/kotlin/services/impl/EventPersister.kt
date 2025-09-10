package nl.vanalphenict.services.impl

import nl.vanalphenict.services.EventRepository
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.EventHandler
import java.time.ZonedDateTime

class EventPersister(val repository: EventRepository) : EventHandler {
    override fun handleStatMessage(
        msg: StatMessage
    ) {
        synchronized(this) {
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