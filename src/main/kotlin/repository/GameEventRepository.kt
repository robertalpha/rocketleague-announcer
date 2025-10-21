package nl.vanalphenict.repository

import kotlin.time.Instant
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.RLAMetaData

class GameEventRepository {

    data class GameEventRecord(
        val timestamp: Instant,
        val gameEventMessage: GameEventMessage,
        val metadataL: RLAMetaData,
    )

    private val gameEventHistory = mutableListOf<GameEventRecord>()

    fun addGameEventMessage(now: Instant, msg: GameEventMessage, metadata: RLAMetaData) {
        gameEventHistory.add(GameEventRecord(now, msg, metadata))
    }

    fun getGameEventHistory(matchGuid: String): List<GameEventRecord> {
        return gameEventHistory.filter { (_, message) -> matchGuid == message.matchGUID }
    }

    fun clear() {
        gameEventHistory.clear()
    }
}
