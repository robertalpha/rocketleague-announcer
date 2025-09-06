package nl.vanalphenict.services

import java.time.ZonedDateTime
import java.util.UUID
import nl.vanalphenict.model.GameEventMessageRoot
import nl.vanalphenict.model.TickerMessageRoot

class EventRepository {
    private val tickerHistory = mutableListOf<Pair<ZonedDateTime,TickerMessageRoot>>()
    private val gameEventHistory = mutableListOf<Pair<ZonedDateTime, GameEventMessageRoot>>()


    fun addTickerMessage(timestamp: ZonedDateTime, tickerMessageRoot: TickerMessageRoot) {
        tickerHistory.add(Pair(timestamp, tickerMessageRoot))
        println("ticker: $tickerMessageRoot")

    }

    fun addGameEvent(timestamp: ZonedDateTime, gameEventRoot: GameEventMessageRoot) {
        gameEventHistory.add(Pair(timestamp, gameEventRoot))
        println("gameEvent: $gameEventRoot")
    }

    // TODO: match UUID missing
    fun getForMatch(uuid: UUID) {

    }

}