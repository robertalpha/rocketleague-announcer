package nl.vanalphenict.repository

import kotlin.time.Instant
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.RLAMetaData

class GameEventRepository {

    data class StoredMetaData(
        val timestamp: Instant,
        val gameEventMessage: GameEventMessage,
        val metadataL: RLAMetaData,
    )

    private val gameEventHistory = mutableListOf<StoredMetaData>()

    fun addGameEventMessage(now: Instant, msg: GameEventMessage, metadata: RLAMetaData) {
        gameEventHistory.add(StoredMetaData(now, msg, metadata))
    }

    fun getGameEventHistory(matchGuid: String): List<StoredMetaData> {
        return gameEventHistory.filter { (_, message) -> matchGuid == message.matchGUID }
    }

    fun clear() {
        gameEventHistory.clear()
    }
}
