package nl.vanalphenict.services

import nl.vanalphenict.model.GameEventMessage
import java.util.UUID
import kotlin.time.Instant
import nl.vanalphenict.model.StatMessage

class EventRepository {
    private val tickerHistory = mutableListOf<Pair<Instant, StatMessage>>()
    private val gameEventHistory = mutableListOf<Pair<Instant, GameEventMessage>>()


    fun addStatMessage(timestamp: Instant, tickerMessageRoot: StatMessage) {
        tickerHistory.add(Pair(timestamp, tickerMessageRoot))
        println("ticker: $tickerMessageRoot")

    }

    fun addGameEventMessage(timestamp: Instant, gameEventRoot: GameEventMessage) {
        gameEventHistory.add(Pair(timestamp, gameEventRoot))
        println("gameEvent: $gameEventRoot")
    }

    // TODO: match UUID missing
    fun getForMatch(uuid: UUID) {

    }

}