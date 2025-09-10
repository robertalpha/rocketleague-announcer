package nl.vanalphenict.services

import nl.vanalphenict.model.GameEventMessage
import java.time.ZonedDateTime
import java.util.UUID
import nl.vanalphenict.model.StatMessage

class EventRepository {
    private val tickerHistory = mutableListOf<Pair<ZonedDateTime, StatMessage>>()
    private val gameEventHistory = mutableListOf<Pair<ZonedDateTime, GameEventMessage>>()


    fun addStatMessage(timestamp: ZonedDateTime, tickerMessageRoot: StatMessage) {
        tickerHistory.add(Pair(timestamp, tickerMessageRoot))
        println("ticker: $tickerMessageRoot")

    }

    fun addGameEventMessage(timestamp: ZonedDateTime, gameEventRoot: GameEventMessage) {
        gameEventHistory.add(Pair(timestamp, gameEventRoot))
        println("gameEvent: $gameEventRoot")
    }

    // TODO: match UUID missing
    fun getForMatch(uuid: UUID) {

    }

}