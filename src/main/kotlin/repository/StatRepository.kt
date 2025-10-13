package nl.vanalphenict.repository

import kotlin.time.Instant
import kotlinx.coroutines.runBlocking
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.triggerSSE

class StatRepository {

    companion object {
        fun List<Pair<Instant, StatMessage>>.filterType(messageType: StatEvents) = this.filter { messageType.eq(it.second.event) }
        fun List<Pair<Instant, StatMessage>>.sortedDescending() = this.sortedByDescending { it.first }
    }

    private val statHistory = mutableListOf<Pair<Instant, StatMessage>>()

    fun addStatMessage(timestamp: Instant, message: StatMessage) {
        statHistory.add(Pair(timestamp, message))
        runBlocking {
            triggerSSE(SSE_EVENT_TYPE.NEW_ACTION)
        }
    }

    fun getStatHistory(matchGuid: String): List<Pair<Instant, StatMessage>> {
        return statHistory.filter { (_, message) -> matchGuid == message.matchGUID }
    }

    fun getLatestMatch(): List<Pair<Instant, StatMessage>> {
        return statHistory.maxByOrNull { it.first }?.second?.matchGUID?.let { getStatHistory(it) }.orEmpty()
    }

    fun findByHash(hashCode: Long): StatMessage? {
        return statHistory.firstOrNull { hashCode == it.first.toEpochMilliseconds() + it.second.hashCode() }?.second
    }

    fun clear() {
        statHistory.clear()
    }
}