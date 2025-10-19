package nl.vanalphenict.repository

import kotlin.time.Instant
import nl.vanalphenict.model.GameEventMessage

class GameEventRepository {

    private val gameEventHistory = mutableListOf<Pair<Instant, GameEventMessage>>()

    fun addGameEventMessage(now: Instant, msg: GameEventMessage) {
        gameEventHistory.add(Pair(now, msg))
    }

    fun getGameEventHistory(matchGuid: String): List<Pair<Instant, GameEventMessage>> {
        return gameEventHistory.filter { (_, message) -> matchGuid == message.matchGUID }
    }

    fun clear() {
        gameEventHistory.clear()
    }
}
