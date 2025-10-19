package nl.vanalphenict.services

import kotlin.time.Duration.Companion.seconds
import nl.vanalphenict.model.GameTimeMessage

class GameTimeTrackerService {

    private val gameTimes = HashMap<String, GameTimeMessage>()

    fun storeGameTime(message: GameTimeMessage) {
        synchronized(this) { gameTimes[message.matchGUID] = message }
    }

    fun getGameTime(matchGUID: String): GameTimeMessage {
        synchronized(this) {
            return gameTimes[matchGUID] ?: GameTimeMessage(matchGUID, 300.seconds, false)
        }
    }
}
