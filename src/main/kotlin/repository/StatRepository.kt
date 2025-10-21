package nl.vanalphenict.repository

import kotlin.collections.filter
import kotlin.collections.sortedByDescending
import kotlin.time.Instant
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage

class StatRepository {

    data class StatMessageRecord(
        val timestamp: Instant,
        val message: StatMessage,
        val metatada: RLAMetaData,
    )

    companion object {
        fun List<StatMessageRecord>.filterType(messageType: StatEvents) =
            this.filter { messageType == it.message.event }

        fun List<StatMessageRecord>.sortedDescending() = this.sortedByDescending { it.timestamp }
    }

    private val statHistory = mutableListOf<StatMessageRecord>()

    fun addStatMessage(timestamp: Instant, message: StatMessage, metatada: RLAMetaData) {
        statHistory.add(StatMessageRecord(timestamp, message, metatada))
    }

    fun getStatHistory(matchGuid: String): List<StatMessageRecord> {
        return statHistory.filter { (_, message) -> matchGuid == message.matchGUID }
    }

    fun getLatestMatch(): List<StatMessageRecord> {
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
