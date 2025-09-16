package nl.vanalphenict.repository

import kotlin.time.Instant
import nl.vanalphenict.model.StatMessage

class StatRepository {

    private val statHistory = mutableListOf<Pair<Instant, StatMessage>>()

    fun addStatMessage(timestamp: Instant, message: StatMessage) {
        statHistory.add(Pair(timestamp, message))
    }

    fun getStatHistory(matchGuid: String): List<Pair<Instant, StatMessage>> {
        return statHistory.filter { (_, message) -> matchGuid == message.matchGUID }
    }






    fun clear() {
        statHistory.clear()
    }
}