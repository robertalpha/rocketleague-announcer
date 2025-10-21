package nl.vanalphenict.repository

import kotlin.time.Instant
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage

class StatRepository {

    data class StoredStatMessage(
        val timestamp: Instant,
        val message: StatMessage,
        val metatada: RLAMetaData,
    )

    companion object {
        fun List<StoredStatMessage>.filterType(messageType: StatEvents) =
            this.filter { messageType == it.message.event }

        fun List<StoredStatMessage>.sortedDescending() = this.sortedByDescending { it.timestamp }
    }

    private val statHistory = mutableListOf<StoredStatMessage>()

    fun addStatMessage(timestamp: Instant, message: StatMessage, metatada: RLAMetaData) {
        statHistory.add(StoredStatMessage(timestamp, message, metatada))
    }

    fun getStatHistory(matchGuid: String): List<StoredStatMessage> {
        return statHistory.filter { (_, message) -> matchGuid == message.matchGUID }
    }

    fun getLatestMatch(): List<StoredStatMessage> {
        return statHistory
            .maxByOrNull { it.timestamp }
            ?.message
            ?.matchGUID
            ?.let { getStatHistory(it) }
            .orEmpty()
    }

    fun findByHash(hashCode: Long): StatMessage? {
        return statHistory
            .firstOrNull { hashCode == it.timestamp.toEpochMilliseconds() + it.message.hashCode() }
            ?.message
    }

    fun clear() {
        statHistory.clear()
    }
}
