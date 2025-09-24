package nl.vanalphenict.repository

import nl.vanalphenict.model.GameEventMessage
import kotlin.time.Instant

class GameEventRepository {

    private val gameEventHistory = mutableListOf<Pair<Instant, GameEventMessage>>()

    fun addGameEventMessage(now: Instant, msg: GameEventMessage) {
        gameEventHistory.add(Pair(now, msg))
    }

    fun getGameEventHistory(matchGuid: String): List<Pair<Instant, GameEventMessage>> {
        return gameEventHistory.filter { (_, message) -> matchGuid == message.matchGUID }
    }
}