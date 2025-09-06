package nl.vanalphenict.services

import java.time.ZonedDateTime
import nl.vanalphenict.model.GameEventMessageRoot
import nl.vanalphenict.model.TickerMessageRoot

class GameEventHandler(val repository: EventRepository) {
    fun handleTicker(
        timestamp: ZonedDateTime,
        tickerMessage: TickerMessageRoot
    ) {
        synchronized(this) {
            // do logic

            repository.addTickerMessage(timestamp, tickerMessage)
        }
    }

    fun handleGameEvent(
        timestamp: ZonedDateTime,
        gameEventMessage: GameEventMessageRoot
    ) {
        synchronized(this) {

            // do logic


            repository.addGameEvent(timestamp, gameEventMessage)
        }

    }

}