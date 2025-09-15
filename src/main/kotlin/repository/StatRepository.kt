package nl.vanalphenict.repository

import nl.vanalphenict.model.StatMessage

class StatRepository {

    private val statHistory = mutableListOf<Pair<Long, StatMessage>>()

    fun addStatMessage(timestamp: Long, message: StatMessage) {
        statHistory.add(Pair(timestamp, message))
    }

    fun getStatHistory(matchGuid: String): List<Pair<Long, StatMessage>> {
        return statHistory.filter { (_, message) -> matchGuid == message.matchGUID }
    }






    fun clear() {
        statHistory.clear()
    }
}